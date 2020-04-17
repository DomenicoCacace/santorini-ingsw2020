package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.response.MessageResponse;

public class WinnerDeclaredResponse extends MessageResponse {

    public final String payload;

    public WinnerDeclaredResponse(String outcome, String payload) {
        super(outcome, "broadcast", Content.WINNER);
        this.payload = payload;
    }

}
