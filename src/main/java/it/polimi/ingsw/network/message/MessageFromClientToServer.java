package it.polimi.ingsw.network.message;

import it.polimi.ingsw.controller.ServerMessageManagerVisitor;

/**
 * Abstract Message type
 * <p>
 * Generalizes all the messages travelling from the clients to the server;
 * <br>
 * Defines the abstract method <i>callVisitor</i>, to implement the
 * <a href="https://archive.org/details/designpatternsel00gamm/page/351">Visitor pattern</a>.
 *
 * @see it.polimi.ingsw.network.server.VirtualClient
 * @see it.polimi.ingsw.controller.MessageManagerParser
 */
public abstract class MessageFromClientToServer extends Message {
    public MessageFromClientToServer(String username, Type type) {
        super(username, type);
    }

    public abstract void callVisitor(ServerMessageManagerVisitor visitor);
}
