package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.Message;

public class User {
    private final String username;
    private final VirtualClient virtualClient;

    public User(VirtualClient virtualClient) {
        this.virtualClient = virtualClient;
        this.username = virtualClient.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public VirtualClient getVirtualClient() {
        return virtualClient;
    }

    public void notify(Message message) {
        virtualClient.notify(message);
    }

    public void closeConnection() {
        virtualClient.closeConnection();
    }

}
