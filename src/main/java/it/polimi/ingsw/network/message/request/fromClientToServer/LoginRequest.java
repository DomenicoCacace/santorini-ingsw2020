package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.Message;

public class LoginRequest extends Message {

    public LoginRequest(String username) {
        super(username, Content.LOGIN);
    }
}
