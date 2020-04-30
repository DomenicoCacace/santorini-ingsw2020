package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

public class LoginResponse extends MessageFromServerToClient {
    private final String outcome;

    @JsonCreator
    public LoginResponse(@JsonProperty("outcome") String outcome, @JsonProperty("username") String username) {
        super(username, Content.LOGIN);
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onLogin(this);
    }
}
