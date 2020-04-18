package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class ChooseNumberOfPlayersRequest extends Message {

    public ChooseNumberOfPlayersRequest(String username) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
    }
}
