package it.polimi.ingsw.network.Client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.response.fromServerToClient.LoginResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.PlayerMoveResponse;
import it.polimi.ingsw.view.ViewInterface;

public class MessageParser {

    private Client client;
    private ViewInterface view;

    public MessageParser(Client client) {
        this.client = client;
    }

    public void parseMessageFromServerToClient(Message message){
        switch (message.content){
            case LOGIN:
                if(((LoginResponse) message).outcome.equals("OK")){
                    // view.displayLobby;
                    System.out.println("Successfull login");
                }
                else{
                    // view.displayError;
                    // view.displayLogin;
                }
                break;
            case PLAYER_MOVE:
                if(((PlayerMoveResponse) message).outcome.equals("OK")){
                    //view.displayGameboard(payload);
                    //payload to be saved internally on the view
                }
                break;
            case PLAYER_BUILD:

                break;
            case END_TURN:

                break;
            case ADD_WORKER:

                break;
            case CHOOSE_INITIAL_GODS:

                break;
            case ASSIGN_GOD:

                break;
            case WINNER_DECLARED:

                break;
            case PLAYER_REMOVED:

                break;
            case CHOOSE_PLAYER_NUMBER:

                break;
            case CHOOSE_GOD:

                break;
            case SELECT_WORKER:

                break;
            case WALKABLE_CELLS:

                break;
            case BUILDABLE_CELLS:

                break;
            case CHOSEN_GODS:

                break;
            case GAME_START:

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.content);
        }
    }
}
