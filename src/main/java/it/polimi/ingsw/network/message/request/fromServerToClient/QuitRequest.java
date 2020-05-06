package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

public class QuitRequest extends MessageFromServerToClient {

    public QuitRequest(String username, Type type) {
        super(username, Type.SERVER_REQUEST);
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onQuit(this);
    }
}
