package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.fromClientToServer.*;
import it.polimi.ingsw.network.message.fromServerToClient.*;
import it.polimi.ingsw.network.message.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.inputManagers.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Handles incoming messages from the server and their respective responses
 */
public class MessageManagerParser implements ClientMessageManagerVisitor {

    private final Client client;
    private final ViewInterface view;
    private int chosenSize;
    private List<Cell> gameBoard;
    private Worker selectedWorker;
    private Cell selectedCell;
    private InputManager inputManager;
    private boolean isCreatingLobby = false;
    private boolean isLookingForLobbies = false;

    /**
     * Default constructor
     *
     * @param client the client to parse messages for
     */
    public MessageManagerParser(Client client) {
        this.client = client;
        this.view = client.getView();
    }

    /**
     * <i>selectedCell</i> setter
     *
     * @param selectedCell the cell to be used for the next action
     */
    public void setSelectedCell(Cell selectedCell) {
        this.selectedCell = selectedCell;
    }

    /**
     * <i>chosenSize</i> setter
     * <p>
     * This is crucial to correctly manage the
     *
     * @param chosenSize the lobby size
     */
    public void setChosenSize(int chosenSize) {
        this.chosenSize = chosenSize;
    }

    /**
     * <i>creatingLobby</i> setter
     * <p>
     * Used to determine if the user is creating a lobby
     *
     * @param creatingLobby a boolean value
     */
    public void setCreatingLobby(boolean creatingLobby) {
        isCreatingLobby = creatingLobby;
    }

    /**
     * <i>lookingForLobbies</i> setter
     * <p>
     * Used to determine if the user is looking for a lobby
     *
     * @param lookingForLobbies a boolean value
     */
    public void setLookingForLobbies(boolean lookingForLobbies) {
        isLookingForLobbies = lookingForLobbies;
    }


    /**
     * Manages the login response
     * <p>
     * ed on the {@linkplain LoginResponse} outcome
     * <ul>
     *     <li>{@linkplain Type#OK}: login successful, the user is in the waiting room and asked to create/join a lobby</li>
     *     <li>{@linkplain Type#SERVER_FULL}: the server has exceeded its maximum capacity, won't accept new connections</li>
     *     <li>{@linkplain Type#INVALID_NAME}: the username is already taken or forbidden</li>
     * </ul>
     * At this stage, the method also tries to save the address+username combo in a file, for quick access on the
     * next login.
     *
     * @param message the message to handle
     */
    @Override
    public void onLogin(LoginResponse message) {

        switch (message.getType()) {
            case OK:
                view.showSuccessMessage("Login successful!");
                try {
                    client.writeSettingsToFile(client.getIpAddress(), client.getUsername());
                } catch (IOException e) {
                    view.showErrorMessage("Could not save credentials");
                }
                enterLobby(message.getLobbies());
                break;
            case SERVER_FULL:
                view.showErrorMessage("Error " + message.getType());
                if (!message.getType().equals(Type.SERVER_FULL))
                    view.showErrorMessage("Login error, please retry");
                client.stopConnection();
                break;
        }
    }

    /**
     * Lets the user decide to join or create a lobby
     *
     * @param lobbiesAvailable the list of available lobbies
     * @see LoginManager
     */
    public void enterLobby(Map<String, List<String>> lobbiesAvailable) {
        isLookingForLobbies = true;
        inputManager = new LobbyInputManager(client, lobbiesAvailable, this, false);
        view.setInputManager(inputManager);
        List<String> options = new LinkedList<>();
        options.add("Create lobby");
        if (lobbiesAvailable.keySet().size() > 0) {
            options.add("Join lobby");
        }
        view.lobbyOptions(options);
        inputManager.startTimer(60);
    }

    /**
     * Notifies the user about the creation of a new lobby
     * <p>
     * If the user is looking for a lobby to join, the list of available lobbies is automatically refreshed and showed
     *
     * @param message the message to handle
     */
    @Override
    public void createLobby(LobbyCreatedEvent message) {
        if (!isCreatingLobby) {
            if (isLookingForLobbies) {
                try {
                    inputManager.stopTimer();
                    inputManager = new LobbyInputManager(client, message.getLobbies(), this, true);
                    view.setInputManager(inputManager);
                    view.chooseLobbyToJoin(message.getLobbies());
                    inputManager.startTimer(60);

                } catch (CancellationException exception) {
                    //
                }
            } else if (message.getType().equals(Type.OK)) {
                view.showSuccessMessage("You created lobby");
                view.showSuccessMessage("Waiting for other players to connect");
            } else {
                view.showErrorMessage(message.getType().toString()); //TODO: replace with standardized message
                enterLobby(message.getLobbies());
            }
        }
    }

    /**
     * Notifies the user about a new user joining the lobby
     *
     * @param message the message to handle
     */
    @Override
    public void joinLobby(UserJoinedLobbyEvent message) {
        if (!client.getUsername().equals(message.getJoinedUser()) && message.getJoinedUser() != null)
            view.showSuccessMessage("The user " + message.getJoinedUser() + " joined the lobby!");
        else {
            switch (message.getType()) {
                case OK:
                    isLookingForLobbies = false;
                    view.showSuccessMessage("You entered lobby");
                    chosenSize = message.getLobbySize();
                    client.setCurrentPlayer(false);
                    break;
                case NO_LOBBY_AVAILABLE:
                    view.showErrorMessage("The lobby isn't available anymore, it was deleted or the game started");
                    enterLobby(message.getAvailableLobbies());
                    break;
                case INVALID_NAME:
                    isCreatingLobby = false;
                    isLookingForLobbies = false;
                    view.showErrorMessage("Username not valid");
                    try {
                        inputManager = new LoginManager(client, true);
                        view.setInputManager(inputManager);
                        inputManager.startTimer(60);
                        view.askUsername();
                    } catch (CancellationException exception) {
                        return;
                    }
                    break;
                case LOBBY_FULL:
                    view.showErrorMessage("The lobby is full");
                    enterLobby(message.getAvailableLobbies());
                    //TODO: maybe ask if the user wants to change username or lobby
            }
        }
    }

    /**
     * Asks the user if it wants to restore a previously saved game
     *
     * @param message the message to handle
     */
    @Override
    public void chooseToReloadMatch(ChooseToReloadMatchRequest message) {
        client.setCurrentPlayer(true);
        inputManager = new ReloadGameInputManager(client);
        view.setInputManager(inputManager);
        inputManager.startTimer(60);
        view.chooseMatchReload();
    }

    /**
     * Asks the lobby owner to choose the gods for the match
     *
     * @param message the message to handle
     */
    @Override // Select gods for the match, request
    public void chooseInitialGods(ChooseInitialGodsRequest message) {
        client.setCurrentPlayer(true);
        inputManager = new GodChoiceInputManager(client, message.getGods(), chosenSize);
        view.setInputManager(inputManager);
        inputManager.startTimer(120);
        view.chooseGameGods(message.getGods(), chosenSize);
    }

    /**
     * Notifies the user about the gods chosen for the game
     *
     * @param message the message to handled
     */
    @Override
    public void onGodChosen(ChosenGodsEvent message) {
        if (message.getType().equals(Type.OK)) {
            view.showSuccessMessage("The chosen gods are: ");
            message.getPayload().forEach(godData -> view.showSuccessMessage(godData.getName())); //TODO: create the concatenated string instead of multiple showMessage
        } else
            view.showErrorMessage(message.getType().toString()); //TODO: replace with standardized message
    }

    /**
     * Asks the user to choose its god
     * <br>
     * If there's only one god left to be chosen, it is automatically assigned to the player
     *
     * @param message the message to handle
     */
    @Override  // choosing the player's unique god
    public void chooseYourGod(ChooseYourGodRequest message) {
        client.setCurrentPlayer(true);
        try {
            if (message.getGods().size() == 1) {
                client.sendMessage(new ChooseYourGodResponse(client.getUsername(), message.getGods().get(0)));
                view.showSuccessMessage("Your God is: " + message.getGods().get(0).getName());
                return;
            }
            inputManager = new GodChoiceInputManager(client, message.getGods(), 1);
            view.setInputManager(inputManager);
            inputManager.startTimer(60);
            view.chooseUserGod(message.getGods());
        } catch (CancellationException ignored) {
        }
    }

    /**
     * Asks the user to select the player which plays first
     *
     * @param message the message to handle
     */
    @Override
    public void chooseStartingPlayer(ChooseStartingPlayerRequest message) {
        client.setCurrentPlayer(true);
        inputManager = new ChooseStartingPlayerInputManager(client, message.getPayload());
        view.setInputManager(inputManager);
        inputManager.startTimer(60);
        view.chooseStartingPlayer(message.getPayload());
    }

    /**
     * Notifies the user that it's been moved to the waiting room upon an opponent's disconnection
     *
     * @param message the message to handle
     */
    @Override
    public void onMovedToWaitingRoom(MovedToWaitingRoomResponse message) {
        client.setCurrentPlayer(true);
        isLookingForLobbies = false;
        isCreatingLobby = false;
        if (message.getDisconnectedUser() != null)
            view.showErrorMessage("The player " + message.getDisconnectedUser() + " disconnected from the game; moved to waiting room");
        inputManager.stopTimer();
        ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);
        ex.schedule(() -> enterLobby(message.getAvailableLobbies()), 10, TimeUnit.SECONDS);
    }

    /**
     * Sends a {@linkplain AddWorkerRequest} to the server to add a new Worker
     *
     * @param row the worker cell row index
     * @param col the worker cell column index
     */
    public void addWorker(int row, int col) {
        client.sendMessage(new AddWorkerRequest(client.getUsername(), gameBoard.get(5 * row + col)));
        client.setCurrentPlayer(false);
    }

    /**
     * Refreshes the game board
     *
     * @param message the message to handle
     */
    @Override
    public void onGameBoardUpdate(GameBoardUpdate message) {
        gameBoard = message.getGameBoard();
        view.initGameBoard(message.getGameBoard());
    }

    /**
     * Manages a PlayerMoveEvent
     * <p>
     * d on the {@linkplain PlayerMoveEvent} outcome:
     * <ul>
     *     <li>{@linkplain Type#OK}: updates the saved game board</li>
     *     <li>Any other case: shows an error message</li>
     * </ul>
     *
     * @param message the message to handle
     */
    @Override // Move action response
    public void onPlayerMove(PlayerMoveEvent message) {
        if (message.getType().equals(Type.OK))
            gameBoard = message.getPayload();
            //payload to be saved internally on the view
        else
            view.showErrorMessage(message.getType().toString()); //TODO: replace with standardized message
        view.showGameBoard(gameBoard);
    }

    /**
     * Manages a PlayerBuildEvent
     * <p>
     * d on the {@linkplain PlayerBuildEvent} outcome:
     * <ul>
     *     <li>{@linkplain Type#OK}: updates the saved game board</li>
     *     <li>Any other case: shows an error message</li>
     * </ul>
     *
     * @param message the message to handle
     */
    @Override // Build action response
    public void onPlayerBuild(PlayerBuildEvent message) {
        if (message.getType().equals(Type.OK))
            gameBoard = message.getPayload();
        else
            view.showErrorMessage(message.getType().toString());
        view.showGameBoard(gameBoard);
    }

    /**
     * Manages an EndTurnEvent
     * <p>
     * If the outcome is {@linkplain Type#OK}, the previous turn has been ended and a new one begun; if the user's
     * username is the same contained in the message, its {@linkplain Client#setCurrentPlayer(boolean)} token is
     * set to {@code true} and its turn begins
     *
     * @param message the message to handle
     */
    @Override // End turn response
    public void onTurnEnd(EndTurnEvent message) {
        if (message.getType().equals(Type.OK)) {
            client.setCurrentPlayer(message.getPayload().equals(client.getUsername()));

            if (client.getUsername().equals(message.getPayload())) { //if currentPlayer
                client.setCurrentPlayer(true);
                inputManager = new SelectWorkerInputManager(client, this);
                view.setInputManager(inputManager);
                inputManager.startTimer(60);
                view.chooseWorker();
            } else
                client.setCurrentPlayer(false);
        } else
            view.showErrorMessage("You can't end your turn now.");
    }

    /**
     * Asks the user to choose where to place its worker
     *
     * @param message the message to handle
     */
    @Override // Place worker request
    public void chooseYourWorkerPosition(ChooseWorkerPositionRequest message) {
        client.setCurrentPlayer(true);
        inputManager = new AddWorkersInputManager(client, this);
        view.setInputManager(inputManager);
        inputManager.startTimer(60);
        view.placeWorker();
    }

    /**
     * Manages a WorkerAddedEvent
     * <p>
     * d on the {@linkplain WorkerAddedEvent} outcome:
     * <ul>
     *     <li>{@linkplain Type#OK}: updates the saved game board</li>
     *     <li>Any other case: shows an error message</li>
     * </ul>
     *
     * @param message the message to handle
     */
    @Override  // Place worker response
    public void onWorkerAdd(WorkerAddedEvent message) {
        if (message.getType().equals(Type.OK)) {
            gameBoard = message.getPayload();
            view.showSuccessMessage("\nWorker placed!");
            view.showGameBoard(message.getPayload());
        } else
            view.showErrorMessage("Can't place a worker in that cell :(");
    }

    /**
     * Manages a GameStartEvent
     * <p>
     * Signals the users the game started; the player designated as <i>first player</i>
     * (see {@linkplain ChooseStartingPlayerRequest}) is asked to perform an action
     *
     * @param message the message to handle
     */
    @Override
    public void onGameStart(GameStartEvent message) {
        inputManager = new SelectWorkerInputManager(client, this);
        view.gameStartScreen(message.getPayload().getBoard());
        gameBoard = message.getPayload().getBoard();
        if (message.getPayload().getCurrentTurn().getCurrentPlayer().getName().equals(client.getUsername())) {
            client.setCurrentPlayer(true);
            view.setInputManager(inputManager);
            inputManager.startTimer(60);
            view.chooseWorker();
            inputManager.setWaitingForInput(true);
        } else
            client.setCurrentPlayer(false);
    }

    /**
     * Asks the user to choose a worker
     *
     * @param row the cell row index
     * @param col the cell column index
     */
    public void chooseWorker(int row, int col) {
        if (gameBoard.get(5 * row + col).getOccupiedBy() != null) {
            selectedWorker = gameBoard.get(5 * row + col).getOccupiedBy();
            Message selectWorkerRequest = new SelectWorkerRequest(client.getUsername(), selectedWorker);
            client.sendMessage(selectWorkerRequest);
        } else {
            view.showErrorMessage("There's no worker in that cell!");
            view.chooseWorker();
            inputManager.setWaitingForInput(true);
        }
    }

    /**
     * Sends a {@linkplain PlayerMoveRequest} to the server
     *
     * @param selectedCell the cell to move the selected worker to
     */
    public void sendMove(Cell selectedCell) {
        client.sendMessage(new PlayerMoveRequest(client.getUsername(), selectedCell, selectedWorker));
        client.setCurrentPlayer(false);
    }

    /**
     * Sends a {@linkplain PlayerBuildRequest} to the server
     *
     * @param selectedBlock block to be built on the target cell
     */
    public void sendBuild(Block selectedBlock) {
        client.sendMessage(new PlayerBuildRequest(client.getUsername(), selectedCell, selectedBlock, selectedWorker));
        client.setCurrentPlayer(false);
    }

    /**
     * Manages a WinnerDeclaredEvent, showing the winner
     *
     * @param message the message to handle
     */
    @Override // Winner declaration, received by all users
    public void onWinnerDeclared(WinnerDeclaredEvent message) {
        if (message.getPayload().equals(client.getUsername()))
            view.showSuccessMessage("You won!!");
        else
            view.showSuccessMessage(message.getPayload() + " WON!");//view.displayWinner
    }

    /**
     * Manages a PlayerRemovedEvent, showing the winner
     *
     * @param message the message to handle
     */
    @Override  // Player removed, received by all users
    public void onPlayerRemoved(PlayerRemovedEvent message) {
        gameBoard = message.getGameBoard();
        if (message.getPayload().equals(client.getUsername())) {
            view.showErrorMessage("You lost");
            client.setCurrentPlayer(false);
        } else {
            view.showGameBoard(message.getGameBoard());
            view.showSuccessMessage(message.getPayload() + " lost");
        }
    }

    /**
     * Manages a WorkerSelectedResponse
     * <p>
     * If the response type is {@linkplain Type#OK}, the user is asked to choose the action to perform; otherwise, a
     * error message is shown
     *
     * @param message the message to handle
     */
    @Override
    public void onWorkerSelected(WorkerSelectedResponse message) {
        client.setCurrentPlayer(true);
        if (message.getType().equals(Type.OK)) {
            if (message.getPossibleActions().size() == 1) {
                selectedWorker = message.getSelectedWorker();
                messageToSend(message.getPossibleActions().get(0));
            } else {
                selectedWorker = message.getSelectedWorker();
                inputManager = new SelectActionInputManager(client, message.getPossibleActions(), this);
                view.setInputManager(inputManager);
                inputManager.startTimer(60);
                view.chooseAction(message.getPossibleActions());
            }
        } else {
            view.showErrorMessage("Wrong worker selected");
            view.chooseWorker();
            inputManager.setWaitingForInput(true);
        }
    }

    /**
     * Manages a WalkableCellsResponse
     * <p>
     * If the response type is {@linkplain Type#OK}, the user is shown a game board highlighting the cells on which
     * its chosen worker can be moved; otherwise, an error message is shown
     *
     * @param message the message to visit
     */
    @Override
    public void onWalkableCellsReceived(WalkableCellsResponse message) {
        if (message.getType().equals(Type.OK)) {
            inputManager = new SelectActionCellInputManager(client, message.getPayload(), true, this);
            view.setInputManager(inputManager);
            inputManager.startTimer(60);
            view.moveAction(gameBoard, message.getPayload());
        } else {
            //we should never enter here
            view.showErrorMessage(message.getType().toString());
        }
    }

    /**
     * Manages a BuildableCellsResponse
     * <p>
     * If the response type is {@linkplain Type#OK}, the user is shown a game board highlighting the cells on which
     * its chosen worker can build on; otherwise, an error message is shown
     *
     * @param message the message to visit
     */
    @Override
    public void onBuildableCellsReceived(BuildableCellsResponse message) {
        if (message.getType().equals(Type.OK)) {
            inputManager = new SelectActionCellInputManager(client, message.getPayload(), false, this);
            view.setInputManager(inputManager);
            inputManager.startTimer(60);
            view.buildAction(gameBoard, message.getPayload());
        } else {
            //we should never enter here
            view.showErrorMessage(message.getType().toString());
        }
    }

    /**
     * Manages a PossibleBuildingBlockResponse
     * <p>
     * If the response type is {@linkplain Type#OK}, the user is shown a game board highlighting the cells on which
     * its chosen worker can be moved; otherwise, an error message is shown
     *
     * @param message the message to visit
     */
    @Override
    public void onBuildingCellSelected(PossibleBuildingBlockResponse message) {
        inputManager = new SelectBlockInputManager(client, this, message.getBlocks());
        view.setInputManager(inputManager);
        inputManager.startTimer(60);
        view.chooseBlockToBuild(message.getBlocks());
    }

    /**
     * Determines what message to send based on the action chosen by the user
     *
     * @param chosenAction the chosen action
     */
    public void messageToSend(PossibleActions chosenAction) {
        Message nextAction = null;
        switch (chosenAction) {
            case BUILD:
                nextAction = new BuildableCellsRequest(client.getUsername());
                break;
            case MOVE:
                nextAction = new WalkableCellsRequest(client.getUsername());
                break;
            case PASS_TURN:
                nextAction = new EndTurnRequest(client.getUsername());
                break;
            case SELECT_OTHER_WORKER:
                inputManager = new SelectWorkerInputManager(client, this);
                view.setInputManager(inputManager);
                inputManager.startTimer(60);
                view.chooseWorker();
                break;
        }

        if (nextAction != null)
            client.sendMessage(nextAction);
    }

    /**
     * Shows the walkable cells and asks the user where to move its chosen worker
     *
     * @param walkableCells a list of walkable cells
     */
    public void printOnMove(List<Cell> walkableCells) {
        view.moveAction(gameBoard, walkableCells);
    }

    /**
     * Shows the buildable cells and asks the user where to build with its chosen worker
     *
     * @param buildableCells a list of buildable cells
     */
    public void printOnBuild(List<Cell> buildableCells) {
        view.buildAction(gameBoard, buildableCells);
    }


}

