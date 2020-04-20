package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class LoginRequest extends Message {

    @JsonCreator
    public LoginRequest(@JsonProperty("username")String username) {
        super(username, Content.LOGIN);
    }
}
