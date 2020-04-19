package it.polimi.ingsw.network.Client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.view.ViewInterface;

public class MessageParser {

    private final Client client;
    private ViewInterface view;

    public MessageParser(Client client) {
        this.client = client;
    }

    public void parseMessageFromServerToClient(Message message) {
        switch (message.content) {
            case LOGIN:
                if (((LoginResponse) message).outcome.equals("OK")) {
                    // view.displayLobby;
                } else {
                    // view.displayLoginError(outcome);
                    // view.displayLogin;
                }
                break;
            case PLAYER_MOVE:
                if (((PlayerMoveResponse) message).outcome.equals("OK")) {
                    //view.displayGameboard(payload);
                    //payload to be saved internally on the view
                } else{
                    //view.displayIllegalActionError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case PLAYER_BUILD:
                if (((PlayerBuildResponse) message).outcome.equals("OK")) {
                    //view.displayGameboard(payload);
                    //payload to be saved internally on the view
                } else{
                    //view.displayIllegalActionError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case END_TURN:
                if (((EndTurnResponse) message).outcome.equals("OK")) {
                    if(((EndTurnResponse) message).payload.equals(client.getUsername())){
                        client.setCurrentPlayer(true);
                        //view.displayPlayableInterface
                    }
                    else {
                        //view.displayNonPlayableInterface(payload);
                    }
                } else{
                    //view.displayIllegalTurnEndingError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case ADD_WORKER:
                if (((AddWorkerResponse) message).outcome.equals("OK")) {
                    //view.displayGameboard(payload)
                }
                else {
                    //view.displayError(outcome)
                }
                break;
            case CHOOSE_INITIAL_GODS:
                //view.diplayAllGods
                break;
            case WINNER_DECLARED:
                //view.displayWinner
                break;
            case PLAYER_LOST:
                //view.displayNonPlayableInterface
                break;
            case PLAYER_REMOVED:
                //view.displayGameboard(payload)
                break;
            case CHOOSE_PLAYER_NUMBER:
                //view.displayChooseNumberOfPlayer
                break;
            case CHOOSE_GOD:
                //view.displayWaitingLobby
                break;
            case SELECT_WORKER:
                if(((SelectWorkerResponse) message).outcome.equals("OK")){
                    //view.displayChooseAction
                } else {
                    //view.displayError(outcome)
                }
                break;
            case WALKABLE_CELLS:
                if(((WalkableCellsResponse) message).outcome.equals("OK")){
                    //view.displayCellsSuperFiche
                } else {
                    //view.displayError(outcome)
                }
                break;
            case BUILDABLE_CELLS:
                if(((BuildableCellsResponse) message).outcome.equals("OK")){
                    //view.displayCellsSuperFiche
                } else {
                    //view.displayError(outcome)
                }
                break;
            case CHOSEN_GODS:
                if(((ChosenGodsResponse) message).outcome.equals("OK")){
                    //view.displayChosenGods
                } else {
                    //view.displayError(outcome)
                }
                break;
            case GAME_START:
                //view.displayGame(payload)
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.content);
        }
    }
}
