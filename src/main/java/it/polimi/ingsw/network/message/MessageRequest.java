package it.polimi.ingsw.network.message;

import java.io.Serializable;

public class MessageRequest implements Serializable {

    public final String username;
    public final Content content;

    public MessageRequest(String username, Content content){
        this.username = username;
        this.content = content;
    }

    public enum Content {
        LOGIN, PLAYER_MOVE, PLAYER_BUILD, GAMEBOARD_STATE, QUEUE, GAME_START,
        END_TURN, ADD_WORKER, CHOOSE_INITIAL_GODS, ASSIGN_GOD
    }
}
