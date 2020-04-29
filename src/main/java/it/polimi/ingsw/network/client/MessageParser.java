package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseInitialGodsRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseStartingPlayerRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseWorkerPositionRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseYourGodRequest;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseNumberOfPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.view.ViewInterface;

import java.util.List;

public class MessageParser {
    private final Client client;
    private final ViewInterface view;
    private int chosenSize;
    private List<Cell> gameboard;
    private Worker selectedWorker;
    private Cell selectedCell;

    public MessageParser(Client client) {
        this.client = client;
        this.view = client.getView();
    }

    public void parseMessageFromServerToClient(Message message) {
        switch (message.getContent()) {

            // Login response confirmation/error
            case LOGIN:
                if (((LoginResponse) message).getOutcome().equals("OK")) {
                    view.showSuccessMessage("Login successful!");
                    // view.displayLobby;
                } else {
                    view.showErrorMessage("Error " + ((LoginResponse) message).getOutcome());
                    if (!((LoginResponse) message).getOutcome().equals("Match already started")) {   // TODO: define constant value
                        view.showErrorMessage("Login error, please retry");
                        view.loginScreen();
                    }
                    view.loginScreen();
                    view.showErrorMessage(((LoginResponse) message).getOutcome());
                }
                break;

            // Move action response
            case PLAYER_MOVE:
                if (((PlayerMoveResponse) message).getOutcome().equals("OK"))
                    gameboard = ((PlayerMoveResponse) message).getPayload();
                    //payload to be saved internally on the view
                else
                    view.showErrorMessage(((PlayerMoveResponse) message).getOutcome());

                view.showGameBoard(gameboard);
                break;

            // Build action response
            case PLAYER_BUILD:
                if (((PlayerBuildResponse) message).getOutcome().equals("OK"))
                    gameboard = ((PlayerBuildResponse) message).getPayload();
                    //payload to be saved internally on the view
                else
                    view.showErrorMessage(((PlayerBuildResponse) message).getOutcome());
                //view.displayBoard(oldBoard)

                view.showGameBoard(gameboard);
                break;

            // End turn response
            case END_TURN:
                if (((EndTurnResponse) message).getOutcome().equals("OK")) {
                    //view.displayPlayableInterface
                    //view.displayNonPlayableInterface(payload);
                    client.setCurrentPlayer(((EndTurnResponse) message).getPayload().equals(client.getUsername()));
                    if (client.getUsername().equals(((EndTurnResponse) message).getPayload()))   //if currentPlayer
                        beginTurn();
                } else {
                    view.showErrorMessage("You can't end your turn now.");
                    //view.displayBoard(oldBoard)
                }
                break;

            // Place worker request
            case WORKER_POSITION:
                client.setCurrentPlayer(true);
                Cell workerCell = view.placeWorker();

                // without taking the cell from the received payload we can overwrite cells, and that's bad.
                Message addWorkerRequest = new AddWorkerRequest(client.getUsername(),
                        ((ChooseWorkerPositionRequest) message).getPayload().get(5 * workerCell.getCoordX() + workerCell.getCoordY()));
                client.sendMessage(addWorkerRequest);
                client.setCurrentPlayer(false);
                break;

            // Place worker response
            case ADD_WORKER:
                if (((AddWorkerResponse) message).getOutcome().equals("OK")) {
                    view.showSuccessMessage("Worker placed!");
                    view.showGameBoard(((AddWorkerResponse) message).getPayload());
                } else {
                    view.showErrorMessage("Can't place a worker in that cell :(");
                    //view.showErrorMessage(((AddWorkerResponse) message).getOutcome());
                }
                break;

            // Select gods for the match, request
            case CHOOSE_INITIAL_GODS:
                client.setCurrentPlayer(true);

                List<GodData> chosenGods = view.chooseGameGods(((ChooseInitialGodsRequest) message).getGods(), chosenSize);
                Message chooseInitialGods = new ChooseInitialGodsResponse(client.getUsername(), chosenGods);
                client.sendMessage(chooseInitialGods);
                client.setCurrentPlayer(false);
                break;

            // Winner declaration, received by all users
            case WINNER_DECLARED:
                view.showSuccessMessage(((WinnerDeclaredResponse) message).getPayload() + " WON!");
                //view.displayWinner
                break;

            // Player removed, received by all users
            case PLAYER_REMOVED: //broadcast
                if (((PlayerRemovedResponse) message).getPayload().equals(client.getUsername()))
                    view.showErrorMessage("You lost");
                else {
                    view.showSuccessMessage(((PlayerRemovedResponse) message).getPayload() + " lost");
                    // board without the loser's workers sent somewhere else
                }
                //view.displayGameboard(payload)
                break;

            case CHOOSE_PLAYER_NUMBER:
                client.setCurrentPlayer(true);
                chosenSize = view.choosePlayersNumber();
                Message numberOfPlayers = new ChooseNumberOfPlayerResponse(client.getUsername(), chosenSize);
                client.sendMessage(numberOfPlayers);
                //view.displayChooseNumberOfPlayer
                break;

            // choosing the player's unique god
            case CHOOSE_GOD:
                client.setCurrentPlayer(true);
                GodData chosenGod = view.chooseUserGod(((ChooseYourGodRequest) message).getGods());
                Message chosenGodMessage = new ChooseYourGodResponse(client.getUsername(), chosenGod);
                client.sendMessage(chosenGodMessage);
                client.setCurrentPlayer(false);
                //view.displayWaitingLobby
                break;
            case CHOSEN_GODS:
                if (((ChosenGodsResponse) message).getOutcome().equals("OK"))
                    view.showSuccessMessage("Choice accepted!");
                    //System.out.println(((ChosenGodsResponse)message).getPayload().toString());
                else
                    view.showErrorMessage(((ChosenGodsResponse) message).getOutcome());
                break;

            // the first player chooses who plays first
            case STARTING_PLAYER:
                client.setCurrentPlayer(true);
                String startingPlayer = view.chooseStartingPlayer(((ChooseStartingPlayerRequest) message).getPayload());
                Message startingPlayerMessage = new ChooseStartingPlayerResponse(client.getUsername(), startingPlayer);
                client.sendMessage(startingPlayerMessage);
                client.setCurrentPlayer(false);
                break;

            // AFTER selecting a worker, this is the response
            case SELECT_WORKER:
                client.setCurrentPlayer(true);
                if (((SelectWorkerResponse) message).getOutcome().equals("OK")) {
                    selectedWorker = ((SelectWorkerResponse) message).getSelectedWorker();
                    PossibleActions action = view.chooseAction(((SelectWorkerResponse) message).getPossibleActions());
                    messageToSend(action);
                    //view.displayChooseAction
                } else {
                    System.out.println("Wrong worker selected");
                    beginTurn();
                    //view.displayError(outcome)
                }
                client.setCurrentPlayer(false);
                break;


            case WALKABLE_CELLS:
                client.setCurrentPlayer(true);
                if (((WalkableCellsResponse) message).getOutcome().equals("OK")) {
                    selectedCell = view.moveAction(gameboard, ((WalkableCellsResponse) message).getPayload());
                    Message moveRequest = new PlayerMoveRequest(client.getUsername(), gameboard.get(5 * selectedCell.getCoordX() + selectedCell.getCoordY()), selectedWorker);
                    client.sendMessage(moveRequest);
                    client.setCurrentPlayer(false);
                } else {
                    //we should never enter here
                    //view.displayError(outcome)
                }
                break;

            case BUILDABLE_CELLS:
                client.setCurrentPlayer(true);
                if (((BuildableCellsResponse) message).getOutcome().equals("OK")) {
                    selectedCell = view.buildAction(gameboard, ((BuildableCellsResponse) message).getPayload());
                    Message blockRequest = new SelectBuildingCellRequest(client.getUsername(), gameboard.get(5 * selectedCell.getCoordX() + selectedCell.getCoordY()));
                    client.sendMessage(blockRequest);
                    client.setCurrentPlayer(false);
                } else {
                    //we should never enter here
                    //view.displayError(outcome)
                }
                break;

            case SELECT_BUILDING_CELL:
                client.setCurrentPlayer(true);
                Block chosenBlock = view.chooseBlockToBuild(((SelectBuildingCellResponse) message).getBlocks());
                Message buildRequest = new PlayerBuildRequest(client.getUsername(), selectedCell, chosenBlock, selectedWorker);
                client.sendMessage(buildRequest);
                client.setCurrentPlayer(false);
                break;

            case GAME_START:
                view.gameStartScreen(((GameStartResponse) message).getPayload().getBoard());
                gameboard = ((GameStartResponse) message).getPayload().getBoard();
                if (((GameStartResponse) message).getPayload().getCurrentTurn().getCurrentPlayer().getName().equals(client.getUsername())) {
                    beginTurn();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.getContent());
        }
    }

    private void beginTurn() {
        client.setCurrentPlayer(true);
        Cell workerCell = view.chooseWorker();
        selectedWorker = gameboard.get(5 * workerCell.getCoordX() + workerCell.getCoordY()).getOccupiedBy();
        Message selectWorkerRequest = new SelectWorkerRequest(client.getUsername(), selectedWorker);
        client.sendMessage(selectWorkerRequest);
        client.setCurrentPlayer(false);
    }


    private void messageToSend(PossibleActions chosenAction) {
        Message nextAction = null;
        switch (chosenAction) {
            case BUILD:
                nextAction = new BuildableCellsRequest(client.getUsername());
                break;
            case MOVE:
                nextAction = new WalkableCellsRequest(client.getUsername());
                break;
            case PASSTURN:
                nextAction = new EndTurnRequest(client.getUsername());
                break;
            case SELECT_OTHER_WORKER:
                beginTurn();
                break;

        }

        if (nextAction != null)
            client.sendMessage(nextAction);
    }

}

