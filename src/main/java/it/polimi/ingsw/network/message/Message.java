package it.polimi.ingsw.network.message;

import java.io.Serializable;

public class Message implements Serializable {
    public final String username;
    public final Content content;

    /**
     * Constructor
     * @param username Who created the message
     * @param content Content of the message
     */

    public Message(String username, Content content){
        this.username = username;
        this.content = content;
    }

    public enum Content {
        LOGIN, PLAYER_ACTION, GAMEBOARD_STATE, QUEUE, GAME_START, END_TURN
    }

    public enum Type {
        REQUEST, OK, FAILURE
    }
}
