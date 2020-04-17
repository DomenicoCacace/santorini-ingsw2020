package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.network.message.request.MessageRequest;

public class ChooseNumberOfPlayersRequest extends MessageRequest {

    public ChooseNumberOfPlayersRequest(String username) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
    }
}
