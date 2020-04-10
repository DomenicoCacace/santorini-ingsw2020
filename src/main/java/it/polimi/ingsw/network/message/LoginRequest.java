package it.polimi.ingsw.network.message;

public class LoginRequest extends MessageRequest {

    public LoginRequest(String username) {
        super(username, Content.LOGIN);
    }
}
