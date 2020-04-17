package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.response.MessageResponse;

public class EndTurnResponse extends MessageResponse {

    public EndTurnResponse(String outcome, String username) {
        super(outcome, username, Content.END_TURN);
    }
}
