package it.polimi.ingsw.shared.messages;

import it.polimi.ingsw.shared.ServerMessageManagerVisitor;
import it.polimi.ingsw.server.network.VirtualClient;

/**
 * Abstract Message type
 * <p>
 * Generalizes all the messages travelling from the clients to the server;
 * <br>
 * Defines the abstract method <i>callVisitor</i>, to implement the
 * <a href="https://archive.org/details/designpatternsel00gamm/page/351">Visitor pattern</a>.
 *
 * @see VirtualClient
 * @see it.polimi.ingsw.server.controller.MessageManagerParser
 */
public abstract class MessageFromClientToServer extends Message {
    public MessageFromClientToServer(String username, Type type) {
        super(username, type);
    }

    public abstract void callVisitor(ServerMessageManagerVisitor visitor);
}
