package it.polimi.ingsw.network.message.response.fromClientToServer;

import it.polimi.ingsw.network.message.Message;

public class ChooseStartingPlayerResponse extends Message {

    private final String payload;

    public ChooseStartingPlayerResponse(String username, String payload) {
        super(username, Content.STARTING_PLAYER);
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
