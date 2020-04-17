package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.response.MessageResponse;

public class LoginResponse extends MessageResponse {

    public LoginResponse(String outcome, String username) {
        super(outcome, username, Content.LOGIN);
    }
}
