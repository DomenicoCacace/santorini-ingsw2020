package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class ChooseStartingPlayerRequest extends Message {

    public List<String> payload;

    public ChooseStartingPlayerRequest(String username, List<String> payload) {
        super(username, Content.STARTING_PLAYER);
        this.payload = payload;
    }
}
