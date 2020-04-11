package it.polimi.ingsw.network.message.response;

import java.io.Serializable;

public class MessageResponse implements Serializable {

    public final String username;
    public final Content content;
    public final String outcome;

    public MessageResponse(String outcome, String username, Content content){
        this.outcome = outcome;
        this.username = username;
        this.content = content;
    }

    public enum Content {
        LOGIN, PLAYER_MOVE, PLAYER_BUILD, GAME_START,
        END_TURN, ADD_WORKER, CHOOSE_INITIAL_GODS, ASSIGN_GOD, CHOOSE_PLAYER_NUMBER,
        CHOSEN_GODS
    }
}
