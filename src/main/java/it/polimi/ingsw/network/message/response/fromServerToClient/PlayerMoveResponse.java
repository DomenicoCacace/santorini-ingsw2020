package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.GameBoard;
import it.polimi.ingsw.network.message.Message;

public class PlayerMoveResponse extends Message {

    public final GameBoard payload;
    public final String outcome;

    public PlayerMoveResponse(String outcome, String username, GameBoard payload) {
        super(username, Content.PLAYER_MOVE);
        this.outcome = outcome;
        if(outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }
}

