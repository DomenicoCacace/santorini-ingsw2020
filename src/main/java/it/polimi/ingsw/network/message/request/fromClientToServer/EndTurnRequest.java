package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.request.MessageRequest;

public class EndTurnRequest extends MessageRequest {

    public EndTurnRequest(String username) {
        super(username, Content.END_TURN);
    }
}
