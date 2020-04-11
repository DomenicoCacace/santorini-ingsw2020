package it.polimi.ingsw.network.message;

public class LoginResponse extends MessageResponse {

    public LoginResponse(String outcome, String username) {
        super(outcome, username, Content.LOGIN);
    }
}
