package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class LoginResponse extends Message {
    public final String outcome;

    public LoginResponse(String outcome, String username) {
        super(username, Content.LOGIN);
        this.outcome = outcome;
    }
}
