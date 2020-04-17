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
        LOGIN, CHOOSE_PLAYER_NUMBER, CHOOSE_INITIAL_GODS, CHOSEN_GODS, ASSIGN_GOD, GAME_START,
        ADD_WORKER, SELECT_WORKER, WALKABLE_CELLS, BUILDABLE_CELLS, PLAYER_REMOVED,
        PLAYER_MOVE, PLAYER_BUILD, END_TURN, WINNER,
    }

}
