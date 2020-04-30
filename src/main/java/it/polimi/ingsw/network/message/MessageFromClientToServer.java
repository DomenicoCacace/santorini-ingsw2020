package it.polimi.ingsw.network.message;

import it.polimi.ingsw.controller.ServerMessageManagerVisitor;

public class MessageFromClientToServer extends Message {
    public MessageFromClientToServer(String username, Content content) {
        super(username, content);
    }

    public void callVisitor(ServerMessageManagerVisitor visitor) {

    }
}
