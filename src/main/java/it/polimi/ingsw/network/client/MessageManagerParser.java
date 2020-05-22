package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.request.fromServerToClient.*;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.inputManagers.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;


public class MessageManagerParser implements ClientMessageManagerVisitor {

    private final Client client;
    private final ViewInterface view;
    private int chosenSize;
    private List<Cell> gameboard;
    private Worker selectedWorker;
    private Cell selectedCell;
    private InputManager inputManager;
    private boolean isCreatingLobby = false;
    private boolean isLookingForLobbies = false;

    public void setSelectedCell(Cell selectedCell) {
        this.selectedCell = selectedCell;
    }

    public void setChosenSize(int chosenSize) {
        this.chosenSize = chosenSize;
    }

    public void setCreatingLobby(boolean creatingLobby) {
        isCreatingLobby = creatingLobby;
    }

    public void setLookingForLobbies(boolean lookingForLobbies) {
        isLookingForLobbies = lookingForLobbies;
    }

    /**
     * Default constructor
     * @param client the client to parse messages for
     */
    public MessageManagerParser(Client client) {
        this.client = client;
        this.view = client.getView();
    }

    /**
     * Manages the login response
     * <p>
     * Based on the {@linkplain LoginResponse} outcome
     *    <ul>
     *        <li>OK: login successful, the user is in the waiting room and asked to create/join a lobby</li>
     *        <li>SERVER_FULL: the server has exceeded its maximum capacity, won't accept new connections</li>
     *        <li>INVALID_NAME: the username is already taken or forbidden</li>
     *    </ul>
     *    At this stage, the method also tries to save the address+username combo in a file, for quick access on the
     *    next login.
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
     * @param lobbiesAvailable the list of available lobbies
     * @see LoginManager
     */
    public void enterLobby(Map<String, List<String>> lobbiesAvailable) {
        client.setCurrentPlayer(true);
        isLookingForLobbies = true;
        inputManager = new LobbyInputManager(client, lobbiesAvailable, this, false);
        view.setInputManager(inputManager);
        List<String> options = new LinkedList<>();
        options.add("Create lobby");
        if (lobbiesAvailable.keySet().size() > 0) {
            options.add("Join lobby");
        }
        view.lobbyOptions(options);
    }

    /**
     * Notifies the user about the creation of a new lobby
     * <p>
     *     If the user is looking for a lobby to join, the list of available lobbies is automatically refreshed and showed
     * @param message the message to handle
     */
    @Override
    public void createLobby(LobbyCreatedEvent message) {
        if(!isCreatingLobby) {
            if (isLookingForLobbies) {
                try {
                    inputManager = new LobbyInputManager(client,message.getLobbies(), this, true);
                    view.setInputManager(inputManager);
                    view.chooseLobbyToJoin(message.getLobbies());
                } catch (CancellationException exception) {
                    //
                }
            } else if (message.getType().equals(Type.OK)) { //TODO: check lobby name
                client.setCurrentPlayer(false);
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
     * @param message the message to handle
     */
    @Override
    public void joinLobby(UserJoinedLobbyEvent message) {
        if(!client.getUsername().equals(message.getJoinedUser()) && message.getJoinedUser()!=null)
            view.showSuccessMessage("The user " + message.getJoinedUser() + " joined the lobby!");
        else {
            switch (message.getType()) {
                case OK:
                    isLookingForLobbies = false;
                    view.showSuccessMessage("You entered lobby");   //TODO: do not show when creating a lobby
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
                        inputManager = new LoginManager(client,true);
                        view.setInputManager(inputManager);
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
     * @param message the message to handle
     */
    @Override
    public void chooseToReloadMatch(ChooseToReloadMatchRequest message) {
        inputManager = new ReloadGameInputManager(client);
        view.setInputManager(inputManager);
        view.chooseMatchReload();
    }

    /**
     * Asks the lobby owner to choose the gods for the match
     * @param message the message to handle
     */
    @Override // Select gods for the match, request
    public void chooseInitialGods(ChooseInitialGodsRequest message) {
        inputManager = new GodChoiceInputManager(client, message.getGods(), chosenSize);
        view.setInputManager(inputManager);
        view.chooseGameGods(message.getGods(), chosenSize);
    }

    /**
     * Notifies the user about the gods chosen for the game
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
     *     If there's only one god left to be chosen, it is automatically assigned to the player
     * @param message the god request to handle
     */
    @Override  // choosing the player's unique god
    public void chooseYourGod(ChooseYourGodRequest message) {
        client.setCurrentPlayer(true);
        try {
            if(message.getGods().size()==1) {
                client.sendMessage(new ChooseYourGodResponse(client.getUsername(), message.getGods().get(0)));
                view.showSuccessMessage("Your God is: " + message.getGods().get(0).getName());
                return;
            }
            inputManager = new GodChoiceInputManager(client, message.getGods(), 1);
            view.setInputManager(inputManager);
            view.chooseUserGod(message.getGods());
        } catch (CancellationException exception) {
            return;
        }
        client.setCurrentPlayer(false);
    }

    @Override
    public void chooseStartingPlayer(ChooseStartingPlayerRequest message) {
        inputManager = new ChooseStartingPlayerInputManager(client, message.getPayload());
        view.setInputManager(inputManager);
        view.chooseStartingPlayer(message.getPayload());
    }

    /**
     * Notifies the user that it's been moved to the waiting room upon an opponent's disconnection
     * @param message the message to handle
     */
    @Override
    public void onMovedToWaitingRoom(MovedToWaitingRoomResponse message) {
        isLookingForLobbies = false;
        isCreatingLobby = false;
        if (message.getDisconnectedUser() != null)
            view.showErrorMessage("The player " + message.getDisconnectedUser() + " disconnected from the game; moved to waiting room");
        enterLobby(message.getAvailableLobbies());
    }

    public void addWorker(int row, int col){
        client.setCurrentPlayer(true);
        client.sendMessage(new AddWorkerRequest(client.getUsername(), gameboard.get(5 * row + col)));
        client.setCurrentPlayer(false);
    }

    @Override
    public void onGameBoardUpdate(GameBoardUpdate message) {
        gameboard = message.getGameBoard();
        view.initGameBoard(message.getGameBoard());
    }

    @Override // Move action response
    public void onPlayerMove(PlayerMoveEvent message) {
        if (message.getType().equals(Type.OK))
            gameboard = message.getPayload();
            //payload to be saved internally on the view
        else
            view.showErrorMessage(message.getType().toString()); //TODO: replace with standardized message
        view.showGameBoard(gameboard);
    }

    @Override // Build action response
    public void onPlayerBuild(PlayerBuildEvent message) {
        if (message.getType().equals(Type.OK))
            gameboard = message.getPayload();
        else
            view.showErrorMessage(message.getType().toString());
        view.showGameBoard(gameboard);
    }

    @Override // End turn response
    public void onTurnEnd(EndTurnEvent message) {
        if (message.getType().equals(Type.OK)) {
            client.setCurrentPlayer(message.getPayload().equals(client.getUsername()));
            if (client.getUsername().equals(message.getPayload())) { //if currentPlayer
               //TODO: client.setCurrentPlayer(true);
                inputManager = new SelectWorkerInputManager(client, this);
                view.setInputManager(inputManager);
                view.chooseWorker();
            }
        } else {
            view.showErrorMessage("You can't end your turn now.");
        }
    }

    @Override // Place worker request
    public void chooseYourWorkerPosition(ChooseWorkerPositionRequest message) {
        inputManager = new AddWorkersInputManager(client,this);
        view.setInputManager(inputManager);
        view.placeWorker();
    }

    @Override  // Place worker response
    public void onWorkerAdd(WorkerAddedEvent message) {
        if (message.getType().equals(Type.OK)) {
            gameboard = message.getPayload();
            view.showSuccessMessage("Worker placed!");
            view.showGameBoard(message.getPayload());
        } else {
            view.showErrorMessage("Can't place a worker in that cell :(");
        }
    }

    @Override
    public void onGameStart(GameStartEvent message) {
        inputManager = new SelectWorkerInputManager(client, this);
        view.gameStartScreen(message.getPayload().getBoard());
        gameboard = message.getPayload().getBoard();
        if (message.getPayload().getCurrentTurn().getCurrentPlayer().getName().equals(client.getUsername())) {
            view.setInputManager(inputManager);
            view.chooseWorker();
            inputManager.setWaitingForInput(true);
        }
    }

    public void chooseWorker(int row, int col){
        client.setCurrentPlayer(true);
        if (gameboard.get(5 * row + col).getOccupiedBy() != null){
            selectedWorker = gameboard.get(5 * row + col).getOccupiedBy();
            Message selectWorkerRequest = new SelectWorkerRequest(client.getUsername(), selectedWorker);
            client.sendMessage(selectWorkerRequest);
        }else{
            view.showErrorMessage("There's no worker in that cell!");
            view.chooseWorker();
            inputManager.setWaitingForInput(true);
        }
        client.setCurrentPlayer(false);
    }

    public void sendMove(Cell selectedCell){
        client.setCurrentPlayer(true);
        client.sendMessage(new PlayerMoveRequest(client.getUsername(), selectedCell, selectedWorker));
        client.setCurrentPlayer(false);
    }

    public void sendBuild(Block selectedBlock){
        client.setCurrentPlayer(true);
        client.sendMessage(new PlayerBuildRequest(client.getUsername(), selectedCell, selectedBlock, selectedWorker));
        client.setCurrentPlayer(false);
    }

    @Override // Winner declaration, received by all users
    public void onWinnerDeclared(WinnerDeclaredEvent message) {
        if (message.getPayload().equals(client.getUsername()))
            view.showSuccessMessage("You won!!");
        else
            view.showSuccessMessage(message.getPayload() + " WON!");//view.displayWinner
    }

    @Override  // Player removed, received by all users
    public void onPlayerRemoved(PlayerRemovedEvent message) {
        gameboard = message.getGameboard();
        if (message.getPayload().equals(client.getUsername()))
            view.showErrorMessage("You lost");

        else {
            view.showGameBoard(message.getGameboard());
            view.showSuccessMessage(message.getPayload() + " lost");
            // board without the loser's workers sent somewhere else
        }
        //view.displayGameboard(payload)
    }

    @Override
    public void onWorkerSelected(WorkerSelectedResponse message) {
        if (message.getType().equals(Type.OK)) {
            if(message.getPossibleActions().size() == 1){
                selectedWorker = message.getSelectedWorker();
                messageToSend(message.getPossibleActions().get(0));
            }
            else {
                selectedWorker = message.getSelectedWorker();
                inputManager = new SelectActionInputManager(client, message.getPossibleActions(), this);
                view.setInputManager(inputManager);
                view.chooseAction(message.getPossibleActions());
            }
        } else {
            view.showErrorMessage("Wrong worker selected");
            view.chooseWorker();
            inputManager.setWaitingForInput(true);
        }
    }

    @Override
    public void onWalkableCellsReceived(WalkableCellsResponse message) {
        if (message.getType().equals(Type.OK)) {
            inputManager = new SelectActionCellInputManager(client, message.getPayload(), true, this);
            view.setInputManager(inputManager);
            view.moveAction(gameboard, message.getPayload());
        } else;
            //we should never enter here
            //view.displayError(outcome)
    }

    @Override
    public void onBuildableCellsReceived(BuildableCellsResponse message) {
        if (message.getType().equals(Type.OK)) {
            inputManager = new SelectActionCellInputManager(client, message.getPayload(), false, this);
            view.setInputManager(inputManager);
            view.buildAction(gameboard, message.getPayload());
            client.setCurrentPlayer(false);
        } else {
            //we should never enter here
            //view.displayError(outcome)
        }
    }

    @Override
    public void onBuildingCellSelected(PossibleBuildingBlockResponse message) {
        inputManager=new SelectBlockInputManager(client, this, message.getBlocks());
        view.setInputManager(inputManager);
        view.chooseBlockToBuild(message.getBlocks());
    }

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
                view.chooseWorker();
                break;
        }
        if (nextAction != null) {
            client.setCurrentPlayer(true);
            client.sendMessage(nextAction);
            client.setCurrentPlayer(false);
        }
    }

    //FIXME: in case the player selects the wrong cell we need to reprint the question
    public void printOnMove(List<Cell> walkableCells){
        view.moveAction(gameboard, walkableCells);
    }

    public void printOnBuild(List<Cell> buildableCells){
        view.buildAction(gameboard, buildableCells);
    }


}

