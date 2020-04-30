package it.polimi.ingsw.network.message;

import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;

public abstract class MessageFromServerToClient extends Message{
    public MessageFromServerToClient(String username, Content content) {
        super(username, content);
    }

    public abstract void callVisitor(ClientMessageManagerVisitor visitor);
}
