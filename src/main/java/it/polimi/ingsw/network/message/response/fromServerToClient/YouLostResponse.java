package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class YouLostResponse extends Message {
    public YouLostResponse(String username) {
        super(username, Content.PLAYER_LOST);
    }
}
