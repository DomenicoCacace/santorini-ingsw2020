package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;

/**
 * Login Request, from client to server
 * <p>
 *     Sends the username chosen by the user to be validated from the server.
 * </p>
 */
public class LoginRequest extends MessageFromClientToServer {

    /**
     * {@inheritDoc}
     */
    public LoginRequest(@JsonProperty("username") String username) {
        super(username, Type.CLIENT_REQUEST);
    }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.login(this);
    }
}
