package it.polimi.ingsw.network.message;

import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;

/**
 * Abstract Message type
 * <p>
 *     Generalizes all the messages travelling from the server to the clients;
 *     <br>
 *         Defines the abstract method <i>callVisitor</i>, to implement the
 *         <a href url=https://archive.org/details/designpatternsel00gamm/page/351>Visitor pattern</a>.
 * </p>
 * @see it.polimi.ingsw.network.client.NetworkHandler
 */
public abstract class MessageFromServerToClient extends Message {

    /**
     * Message constructor
     * @param username the sender's username
     * @param type the message type
     */
    public MessageFromServerToClient(String username, Type type) {
        super(username, type);
    }


    public abstract void callVisitor(ClientMessageManagerVisitor visitor);
}
