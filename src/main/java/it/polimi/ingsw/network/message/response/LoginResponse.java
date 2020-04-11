package it.polimi.ingsw.network.message.response;

public class LoginResponse extends MessageResponse {

    public LoginResponse(String outcome, String username) {
        super(outcome, username, Content.LOGIN);
    }
}
