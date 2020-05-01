package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.request.fromServerToClient.*;
import it.polimi.ingsw.network.message.response.fromClientToServer.*;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.view.ViewInterface;

import java.util.ArrayList;
import java.util.List;


public class MessageParser implements ClientMessageManagerVisitor{
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

    @Override // Login response confirmation/error
    public void onLogin(LoginResponse message) {
        if (message.getOutcome().equals("OK")) {
            view.showSuccessMessage("Login successful!");
            // view.displayLobby;
        } else {
            view.showErrorMessage("Error " + message.getOutcome());
            if (!message.getOutcome().equals("Match already started")) {   // TODO: define constant value
                view.showErrorMessage("Login error, please retry");
                client.setUsername(view.askUsername());
            }
            else {
                view.showErrorMessage(message.getOutcome());
                client.stopConnection();
            }
        }
    }

    @Override // Move action response
    public void onPlayerMove(PlayerMoveResponse message) {
        if (message.getOutcome().equals("OK"))
            gameboard = message.getPayload();
            //payload to be saved internally on the view
        else
            view.showErrorMessage(message.getOutcome());

        view.showGameBoard(gameboard);

    }

    @Override // Build action response
    public void onPlayerBuild(PlayerBuildResponse message) {
        if (message.getOutcome().equals("OK"))
            gameboard = message.getPayload();
            //payload to be saved internally on the view
        else
            view.showErrorMessage(message.getOutcome());
        //view.displayBoard(oldBoard)

        view.showGameBoard(gameboard);
    }

    @Override // End turn response
    public void onTurnEnd(EndTurnResponse message) {
        if (message.getOutcome().equals("OK")) {
            //view.displayPlayableInterface
            //view.displayNonPlayableInterface(payload);
            client.setCurrentPlayer(message.getPayload().equals(client.getUsername()));
            if (client.getUsername().equals(message.getPayload()))   //if currentPlayer
                beginTurn();
        } else {
            view.showErrorMessage("You can't end your turn now.");
            //view.displayBoard(oldBoard)
        }
    }

    @Override // Place worker request
    public void chooseYourWorkerPosition(ChooseWorkerPositionRequest message) {
        client.setCurrentPlayer(true);
        Cell workerCell = view.placeWorker();
        // without taking the cell from the received payload we can overwrite cells, and that's bad.
        Message addWorkerRequest = new AddWorkerRequest(client.getUsername(),
                message.getPayload().get(5 * workerCell.getCoordX() + workerCell.getCoordY()));
        client.sendMessage(addWorkerRequest);
        client.setCurrentPlayer(false);
    }

    @Override  // Place worker response
    public void onWorkerAdd(AddWorkerResponse message) {
        if (message.getOutcome().equals("OK")) {
            view.showSuccessMessage("Worker placed!");
            view.showGameBoard(message.getPayload());
        } else {
            view.showErrorMessage("Can't place a worker in that cell :(");
            //view.showErrorMessage(((AddWorkerResponse) message).getOutcome());
        }
    }

    @Override // Select gods for the match, request
    public void chooseInitialGods(ChooseInitialGodsRequest message) {
        client.setCurrentPlayer(true);

        List<GodData> chosenGods = view.chooseGameGods(message.getGods(), chosenSize);
        Message chooseInitialGods = new ChooseInitialGodsResponse(client.getUsername(), chosenGods);
        client.sendMessage(chooseInitialGods);
        client.setCurrentPlayer(false);
    }

    @Override // Winner declaration, received by all users
    public void onWinnerDeclared(WinnerDeclaredResponse message) {
        view.showSuccessMessage(message.getPayload() + " WON!");//view.displayWinner
    }

    @Override  // Player removed, received by all users
    public void onPlayerRemoved(PlayerRemovedResponse message) {
        if (message.getPayload().equals(client.getUsername()))
            view.showErrorMessage("You lost");
        else {
            view.showSuccessMessage(message.getPayload() + " lost");
            // board without the loser's workers sent somewhere else
        }
        //view.displayGameboard(payload)
    }

    @Override
    public void choosePlayerNumber(ChooseNumberOfPlayersRequest message) {
        client.setCurrentPlayer(true);
        chosenSize = view.choosePlayersNumber();
        Message numberOfPlayers = new ChooseNumberOfPlayerResponse(client.getUsername(), chosenSize);
        client.sendMessage(numberOfPlayers);
        //view.displayChooseNumberOfPlayer
    }

    @Override  // choosing the player's unique god
    public void chooseYourGod(ChooseYourGodRequest message) {
        client.setCurrentPlayer(true);
        GodData chosenGod = view.chooseUserGod(message.getGods());
        Message chosenGodMessage = new ChooseYourGodResponse(client.getUsername(), chosenGod);
        client.sendMessage(chosenGodMessage);
        client.setCurrentPlayer(false);
    }

    @Override
    public void onGodChosen(ChosenGodsResponse message) {
        if (message.getOutcome().equals("OK"))
            view.showSuccessMessage("Choice accepted!");
            //System.out.println(((ChosenGodsResponse)message).getPayload().toString());
        else
            view.showErrorMessage(message.getOutcome());
    }

    @Override
    public void chooseStartingPlayer(ChooseStartingPlayerRequest message) {
        client.setCurrentPlayer(true);
        String startingPlayer = view.chooseStartingPlayer(message.getPayload());
        Message startingPlayerMessage = new ChooseStartingPlayerResponse(client.getUsername(), startingPlayer);
        client.sendMessage(startingPlayerMessage);
        client.setCurrentPlayer(false);
    }

    @Override
    public void onWorkerSelected(SelectWorkerResponse message) {
        client.setCurrentPlayer(true);
        if (message.getOutcome().equals("OK")) {
            selectedWorker = message.getSelectedWorker();
            PossibleActions action = view.chooseAction(message.getPossibleActions());
            messageToSend(action);
            //view.displayChooseAction
        } else {
            System.out.println("Wrong worker selected");
            beginTurn();
            //view.displayError(outcome)
        }
        client.setCurrentPlayer(false);
    }

    @Override
    public void onWalkableCellsReceived(WalkableCellsResponse message) {
        client.setCurrentPlayer(true);
        if (message.getOutcome().equals("OK")) {
            selectedCell = view.moveAction(gameboard, message.getPayload());
            Message moveRequest = new PlayerMoveRequest(client.getUsername(), gameboard.get(5 * selectedCell.getCoordX() + selectedCell.getCoordY()), selectedWorker);
            client.sendMessage(moveRequest);
            client.setCurrentPlayer(false);
        } else {
            //we should never enter here
            //view.displayError(outcome)
        }
    }

    @Override
    public void onBuildableCellsReceived(BuildableCellsResponse message) {
        client.setCurrentPlayer(true);
        if (message.getOutcome().equals("OK")) {
            selectedCell = view.buildAction(gameboard, message.getPayload());
            Message blockRequest = new SelectBuildingCellRequest(client.getUsername(), gameboard.get(5 * selectedCell.getCoordX() + selectedCell.getCoordY()));
            client.sendMessage(blockRequest);
            client.setCurrentPlayer(false);
        } else {
            //we should never enter here
            //view.displayError(outcome)
        }
    }

    @Override
    public void onBuildingCellSelected(SelectBuildingCellResponse message) {
        client.setCurrentPlayer(true);
        Block chosenBlock = view.chooseBlockToBuild(message.getBlocks());
        Message buildRequest = new PlayerBuildRequest(client.getUsername(), selectedCell, chosenBlock, selectedWorker);
        client.sendMessage(buildRequest);
        client.setCurrentPlayer(false);
    }

    @Override
    public void onGameStart(GameStartResponse message) {
        view.gameStartScreen(message.getPayload().getBoard());
        gameboard = message.getPayload().getBoard();
        if (message.getPayload().getCurrentTurn().getCurrentPlayer().getName().equals(client.getUsername())) {
            beginTurn();
        }
    }

    @Override
    public void onQuit(QuitRequest message) {
        //TODO: close connection
        client.stopConnection();
    }

    @Override
    public void chooseToReloadMatch(ChooseToReloadMatchRequest message) {
        Message messageResponse = new ChooseToReloadMatchResponse(client.getUsername(), view.chooseMatchReload());
        client.sendMessage(messageResponse);
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

