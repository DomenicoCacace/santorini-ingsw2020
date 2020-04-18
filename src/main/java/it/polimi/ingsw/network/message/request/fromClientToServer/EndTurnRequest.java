package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.Message;

public class EndTurnRequest extends Message {

    public EndTurnRequest(String username) {
        super(username, Content.END_TURN);
    }
}
