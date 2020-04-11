package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.request.MessageRequest;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VirtualClient {
    private String username;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public String getUsername() {
        return username;
    }

    public void notify(MessageResponse messageResponse) throws IOException {
        oos.reset();
        oos.writeObject(messageResponse);
        oos.flush();
    }

    public void notify(MessageRequest messageRequest) throws IOException {
        oos.reset();
        oos.writeObject(messageRequest);
        oos.flush();
    }
}
