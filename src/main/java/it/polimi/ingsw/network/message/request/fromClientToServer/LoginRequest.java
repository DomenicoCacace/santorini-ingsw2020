package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.request.MessageRequest;

public class LoginRequest extends MessageRequest {

    public LoginRequest(String username) {
        super(username, Content.LOGIN);
    }
}
