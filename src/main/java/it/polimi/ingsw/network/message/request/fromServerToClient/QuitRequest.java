package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

public class QuitRequest extends MessageFromServerToClient {

    public QuitRequest(String username, Content content) {
        super(username, content);
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onQuit(this);
    }
}
