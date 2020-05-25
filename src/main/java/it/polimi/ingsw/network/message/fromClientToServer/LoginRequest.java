package it.polimi.ingsw.network.message.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

/**
 * Login Request, from client to server
 * <p>
 * Sends the username chosen by the user to be validated from the server.
 */
public class LoginRequest extends MessageFromClientToServer {


    public LoginRequest(@JsonProperty("username") String username) {
        super(username, Type.CLIENT_REQUEST);
    }


    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.login(this);
    }
}
