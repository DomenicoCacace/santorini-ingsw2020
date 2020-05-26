package it.polimi.ingsw.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.fromServerToClient.*;
import it.polimi.ingsw.network.server.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Actual game controller
 * <p>
 * class is responsible for changes in the game state: it
 * <ul>
 *     <li>makes actions to alter the game (model), based on the users' inputs received in the {@linkplain #parser}</li>
 *     <li>notifies the clients about the changes, propagated from the model using listeners</li>
 * </ul>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class ServerController implements AddWorkerListener, BuildableCellsListener, BuildActionListener, BuildingBlocksListener,
        EndTurnListener, MoveActionListener, WalkableCellsListener, PlayerLostListener, SelectWorkerListener {

    private final static Logger logger = Logger.getLogger(Logger.class.getName());
    private final GameInterface game;
    private final Map<String, PlayerInterface> playerMap;
    private final MessageManagerParser parser;
    private int cont = 0;
    private File file;

    /**
     * Default constructor
     * <p>
     * Sets the class attributes and the various listeners in Game and Player
     *
     * @param game       the {@linkplain Game} to manage
     * @param players    the {@linkplain Player}s playing the game
     * @param parser     the {@linkplain MessageManagerParser}
     * @param gameToSave a {@linkplain File} to store the game status
     */
    public ServerController(GameInterface game, Map<User, PlayerInterface> players, MessageManagerParser parser, File gameToSave) {
        this.game = game;
        playerMap = new LinkedHashMap<>();
        players.forEach((u, p) -> playerMap.put(u.getUsername(), p));
        this.parser = parser;
        this.file = gameToSave;
        game.addBuildActionListener(this);
        game.addEndTurnListener(this);
        game.addMoveActionListener(this);
        game.addPlayerLostListener(this);
        playerMap.values().forEach(playerInterface
                -> {
            playerInterface.addWorkerListener(this);
            playerInterface.addBuildableCellsListener(this);
            playerInterface.addWalkableCellsListener(this);
            playerInterface.addSelectWorkerListener(this);
            playerInterface.addBuildingBlocksListener(this);
        });
    }


    /**
     * Resumes a previously saved game state
     * <p>
     * Before calling this method, the caller has to make sure to reload an already existing game; this method just
     * sends the correct message based on the saved game state to resume the game, performing no check at all.
     *
     * @see it.polimi.ingsw.network.server.Lobby#reloadMatch(boolean)
     */
    public void handleGameRestore() {
        PlayerData currPlayerData = game.buildGameData().getCurrentTurn().getCurrentPlayer();
        PlayerInterface currPlayerInterface = playerMap.get(currPlayerData.getName());
        if (currPlayerData.getSelectedWorker() != null) { //Now i have selectedWorker inside the playerDataClass
            try {
                parser.parseMessageFromServerToClient(new GameBoardUpdate(ReservedUsernames.BROADCAST.toString(), game.buildBoardData())); //need this otherwise client doesn't have gameBoard saved locally!
                currPlayerInterface.setSelectedWorker(currPlayerData.getSelectedWorker());//simulate a setWorker, in this way the player will call the SelectWorkerlistener.
            } catch (NotYourWorkerException e) {
                logger.log(Level.SEVERE, "Entered a forbidden catch clause while restoring a saved game");
                e.printStackTrace(); //shouldn't go here
            }
        } else
            parser.parseMessageFromServerToClient(new GameStartEvent(Type.OK, game.buildGameData()));
    }

    /**
     * Sets the file to save the game to and saves the current state to it
     *
     * @param file the file to store the game state
     */
    public void setFile(File file) {
        this.file.delete();
        this.file = file;
        saveState();
    }

    /**
     * Adds a worker for the player
     * <p>
     * Tries to add a new worker for the player requesting it. This action can end with one and one only of the following
     * omes:
     * <ul>
     *     <li>the worker is added successfully on the board, but the player still has workers to place: a new
     *     {@linkplain ChooseWorkerPositionRequest} is sent to the same player;</li>
     *     <li>the worker is added successfully on the board, the current player has placed all of his workers, but
     *     there still are players which have to place their workers: a new {@linkplain ChooseWorkerPositionRequest}
     *     is sent to the following player;</li>
     *     <li>the worker is added successfully on the board, all the players have placed their workers: the game can
     *     start, so a {@linkplain GameStartEvent} is sent to the first player;</li>
     *     <li>the worker could not be added to the board, because the cell the player wants to place the worker on
     *     is already taken: a {@linkplain WorkerAddedEvent} is sent with {@linkplain Type#ADDING_FAILED} as outcome,
     *     followed by a {@linkplain ChooseWorkerPositionRequest}.</li>
     * </ul>
     *
     * @param username the user's username
     * @param cell     the cell to add the worker on
     */
    public void addWorker(String username, Cell cell) {
        try {
            playerMap.get(username).addWorker(cell);
            if (!playerMap.get(username).allWorkersArePlaced())
                parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(username, game.buildBoardData()));
            else {
                cont++;
                if (cont < playerMap.values().size()) {
                    List<String> usernames = new ArrayList<>(playerMap.keySet());
                    parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(usernames.get(cont), game.buildBoardData()));
                } else if (!game.hasFirstPlayerLost())
                    parser.parseMessageFromServerToClient(new GameStartEvent(Type.OK, game.buildGameData()));
            }
        } catch (AddingFailedException e) {
            logger.log(Level.INFO, username + " failed to add a worker");
            parser.parseMessageFromServerToClient(new WorkerAddedEvent(Type.ADDING_FAILED, username, game.buildBoardData()));
            parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(username, game.buildBoardData()));
        }
    }

    /**
     * Sets a player's selected worker for the following actions to be performed
     * <p>
     * If the chosen worker is not owned by the user which sent the message, a {@linkplain WorkerSelectedResponse} is
     * sent with {@linkplain Type#NOT_YOUR_WORKER} as outcome; if the player owns the given worker, the model will
     * notify the {@linkplain SelectWorkerListener}s.
     *
     * @param username the user's username
     * @param worker   the worker to set as current
     * @see PlayerInterface#setSelectedWorker(Worker)
     */
    public void selectWorker(String username, Worker worker) {
        try {
            playerMap.get(username).setSelectedWorker(worker);
        } catch (NotYourWorkerException e) {
            parser.parseMessageFromServerToClient(new WorkerSelectedResponse(Type.NOT_YOUR_WORKER, username, null, null));
        }
    }

    /**
     * Provides the cells on which the player's selected worker can move to
     * <p>
     * If the player has selected no worker, a {@linkplain WalkableCellsResponse} is
     * sent with {@linkplain Type#NO_WORKER_SELECTED} as outcome; if the player owns the given worker, the model will
     * notify the {@linkplain WalkableCellsListener}s.
     *
     * @param username the user's username
     * @see PlayerInterface#obtainWalkableCells()
     */
    public void obtainWalkableCells(String username) {
        try {
            playerMap.get(username).obtainWalkableCells();
        } catch (WrongSelectionException e) {
            parser.parseMessageFromServerToClient(new WalkableCellsResponse(Type.NO_WORKER_SELECTED, username, null));
        }
    }

    /**
     * Provides the cells on which the player's selected worker can build on
     * <p>
     * If the player has selected no worker, a {@linkplain BuildableCellsResponse} is
     * sent with {@linkplain Type#NO_WORKER_SELECTED} as outcome; if the player owns the given worker, the model will
     * notify the {@linkplain BuildableCellsListener}s.
     *
     * @param username the user's username
     * @see PlayerInterface#obtainBuildableCells()
     */
    public void obtainBuildableCells(String username) {
        try {
            playerMap.get(username).obtainBuildableCells();
        } catch (WrongSelectionException e) {
            parser.parseMessageFromServerToClient(new BuildableCellsResponse(Type.NO_WORKER_SELECTED, username, null));
        }
    }

    /**
     * Provides the possible blocks which the current player's worker can build
     * <p>
     * If the player cannot make a build action, a {@linkplain PlayerBuildEvent} is
     * sent with {@linkplain Type#ILLEGAL_BUILD} as outcome; if the player can perform a build action, the model will
     * notify the {@linkplain BuildingBlocksListener}s.
     *
     * @param username     the user's username
     * @param selectedCell the cell on which the player wants to build
     * @see PlayerInterface#obtainBuildingBlocks(Cell)
     */
    public void selectBuildingCell(String username, Cell selectedCell) {
        try {
            playerMap.get(username).obtainBuildingBlocks(selectedCell);
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerBuildEvent(Type.ILLEGAL_BUILD, username, game.buildBoardData()));
        }
    }

    /**
     * Handles a movement action
     * <p>
     * If the player cannot perform a movement action, a {@linkplain PlayerMoveEvent} is
     * sent with {@linkplain Type#ILLEGAL_MOVEMENT} as outcome; if the player can perform a movement action,
     * the model will notify the {@linkplain MoveActionListener}s.
     *
     * @param username   the user's username
     * @param moveAction the action to handle
     * @see PlayerInterface#useAction(Action)
     */
    public void handleMoveAction(String username, MoveAction moveAction) {
        try {
            playerMap.get(username).useAction(moveAction);
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerMoveEvent(Type.ILLEGAL_MOVEMENT, username, game.buildBoardData()));
        }

    }

    /**
     * Handles a build action
     * <p>
     * If the player cannot perform a build action, a {@linkplain PlayerBuildEvent} is
     * sent with {@linkplain Type#ILLEGAL_BUILD} as outcome; if the player can perform a build action,
     * the model will notify the {@linkplain BuildActionListener}s.
     *
     * @param username    the user's username
     * @param buildAction the action to handle
     * @see PlayerInterface#useAction(Action)
     */
    public void handleBuildAction(String username, BuildAction buildAction) {
        try {
            playerMap.get(username).useAction(buildAction);
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerBuildEvent(Type.ILLEGAL_BUILD, username, game.buildBoardData()));
        }
    }

    /**
     * Handles a turn end request
     * <p>
     * If the player cannot pass its turn, a {@linkplain EndTurnEvent} is
     * sent with {@linkplain Type#CANNOT_END_TURN} as outcome; if the player can end its turn,
     * the model will notify the {@linkplain EndTurnListener}s.
     *
     * @param username the user's username
     * @see PlayerInterface#askPassTurn()
     */
    public void passTurn(String username) {
        try {
            playerMap.get(username).askPassTurn();
        } catch (IllegalEndingTurnException e) {
            parser.parseMessageFromServerToClient(new EndTurnEvent(Type.CANNOT_END_TURN, username, null));
        }
    }

    /**
     * Saves the current state of the game to a file
     */
    public void saveState() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerFor(Game.class).withDefaultPrettyPrinter().writeValue(file, game);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies all users about a successful movement action
     * <br>
     * Saves the current game state
     *
     * @param cells the changed gameBoard as a List of cells
     * @see Game#validateMoveAction(MoveAction)
     */
    @Override
    public void onMoveAction(List<Cell> cells) {
        saveState();
        parser.parseMessageFromServerToClient(new PlayerMoveEvent(Type.OK, ReservedUsernames.BROADCAST.toString(), cells));
    }

    /**
     * Notifies all users about a successful worker placement
     *
     * @param cells the changed gameBoard as a List of cells
     * @see Player#addWorker(Cell)
     */
    @Override
    public void onWorkerAdd(List<Cell> cells) {
        parser.parseMessageFromServerToClient(new WorkerAddedEvent(Type.OK, ReservedUsernames.BROADCAST.toString(), cells));
    }

    /**
     * Notifies all users about a successful build action
     * <br>
     * Saves the current game state
     *
     * @param cells the changed gameBoard as a List of cells
     * @see Game#validateBuildAction(BuildAction)
     */
    @Override
    public void onBuildAction(List<Cell> cells) {
        saveState();
        parser.parseMessageFromServerToClient(new PlayerBuildEvent(Type.OK, ReservedUsernames.BROADCAST.toString(), cells));
    }

    /**
     * Notifies a single user about the possible cells on which its selected worker can build on
     *
     * @param name  the player to notify's username
     * @param cells the cells on which the player's selected worker can build on
     * @see Game#getBuildableCells(Worker)
     */
    @Override
    public void onBuildableCell(String name, List<Cell> cells) {
        parser.parseMessageFromServerToClient(new BuildableCellsResponse(Type.OK, name, cells));
    }

    /**
     * Notifies a single user about the possible blocks its selected worker can build, if there's more than one
     *
     * @param name   the player to notify's username
     * @param blocks the blocks the player's selected worker can build
     * @see Player#obtainBuildingBlocks(Cell)
     */
    @Override
    public void onBlocksObtained(String name, List<Block> blocks) {
        parser.parseMessageFromServerToClient(new PossibleBuildingBlockResponse(Type.NOTIFY, name, blocks));
    }

    /**
     * Notifies all users about the end of the current turn and the next player who has to play
     * <br>
     * Saves the current state
     *
     * @param name the next player's username
     */
    @Override
    public void onTurnEnd(String name) {
        saveState();
        parser.parseMessageFromServerToClient(new EndTurnEvent(Type.OK, ReservedUsernames.BROADCAST.toString(), name));
    }

    /**
     * Notifies a single user about the possible cells on which its selected worker can walk to
     *
     * @param name  the player to notify's username
     * @param cells the cells on which the player's selected worker can walk to
     * @see Game#getWalkableCells(Worker)
     */
    @Override
    public void onWalkableCells(String name, List<Cell> cells) {
        parser.parseMessageFromServerToClient(new WalkableCellsResponse(Type.OK, name, cells));
    }

    /**
     * Notifies all users that a player lost
     * <p>
     * The file containing the saved game is also deleted, to be replaced with a new one
     *
     *
     * @param username  the player which lost
     * @param gameBoard the changed gameBoard without the loser's workers
     */
    @Override
    public void onPlayerLoss(String username, List<Cell> gameBoard) {
        parser.parseMessageFromServerToClient(new PlayerRemovedEvent(username, gameBoard));
        playerMap.remove(username);
        file.delete();
    }

    /**
     * Notifies a single user that its worker choice was correct
     * <br>
     * Saves the current game state
     *
     * @param username        the player to notify's username
     * @param possibleActions a list of possible actions to be performed with the chosen worker
     * @param selectedWorker  the worker the player selected
     */
    @Override
    public void onSelectedWorker(String username, List<PossibleActions> possibleActions, Worker selectedWorker) {
        saveState();
        parser.parseMessageFromServerToClient(new WorkerSelectedResponse(Type.OK, username, possibleActions, selectedWorker));
    }
}