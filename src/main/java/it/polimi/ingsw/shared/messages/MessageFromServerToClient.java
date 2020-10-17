package it.polimi.ingsw.shared.messages;

import it.polimi.ingsw.shared.ClientMessageManagerVisitor;
import it.polimi.ingsw.client.network.NetworkHandler;

/**
 * Abstract Message type
 * <p>
 * Generalizes all the messages travelling from the server to the clients;
 * <br>
 * Defines the abstract method <i>callVisitor</i>, to implement the
 * <a href="https://archive.org/details/designpatternsel00gamm/page/351">Visitor pattern</a>.
 *
 * @see NetworkHandler
 */
public abstract class MessageFromServerToClient extends Message {

    /**
     * Message constructor
     *
     * @param username the sender's username
     * @param type     the message type
     */
    public MessageFromServerToClient(String username, Type type) {
        super(username, type);
    }

    public abstract void callVisitor(ClientMessageManagerVisitor visitor);
}
