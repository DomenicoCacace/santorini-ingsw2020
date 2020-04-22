package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class PlayerMoveResponse extends Message {

    private final List<Cell> payload;
    private final String outcome;

    public PlayerMoveResponse(String outcome, String username, List<Cell> payload) {
        super(username, Content.PLAYER_MOVE);
        this.outcome = outcome;
        if (outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }

    public List<Cell> getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }
}

