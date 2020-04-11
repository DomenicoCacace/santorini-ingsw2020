package it.polimi.ingsw.network.message.request;

import java.io.Serializable;

public class MessageRequest implements Serializable {

    public final String username;
    public final Content content;

    public MessageRequest(String username, Content content){
        this.username = username;
        this.content = content;
    }

    public enum Content {
        LOGIN, PLAYER_MOVE, PLAYER_BUILD,
        END_TURN, ADD_WORKER, CHOOSE_INITIAL_GODS, ASSIGN_GOD, WINNER_DECLARED,
        PLAYER_REMOVED, CHOOSE_PLAYER_NUMBER, CHOOSE_GOD;
    }
}
