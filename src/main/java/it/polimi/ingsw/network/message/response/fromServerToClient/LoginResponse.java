package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class LoginResponse extends Message {
    public final String outcome;

    @JsonCreator
    public LoginResponse(@JsonProperty("outcome")String outcome,@JsonProperty("username") String username) {
        super(username, Content.LOGIN);
        this.outcome = outcome;
    }
}
