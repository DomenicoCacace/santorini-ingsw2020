package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class EndTurnResponse extends Message {
    private final String outcome;
    private final String payload;

    public EndTurnResponse(String outcome, String username, String payload) {
        super(username, Content.END_TURN);
        this.payload = payload;
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getPayload() {
        return payload;
    }
}
