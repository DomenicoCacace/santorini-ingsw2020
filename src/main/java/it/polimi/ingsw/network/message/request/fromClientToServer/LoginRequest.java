package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;

public class LoginRequest extends MessageFromClientToServer {

    @JsonCreator
    public LoginRequest(@JsonProperty("username") String username) {
        super(username, Content.LOGIN);
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        //This message is managed in virtual client.
    }
}
