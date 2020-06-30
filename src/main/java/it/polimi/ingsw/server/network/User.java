package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.shared.messages.Message;

/**
 * The entity connecting to the server; can be used to keep a game history or something similar
 */
public class User {
    private final VirtualClient virtualClient;
    private final Server server;
    private String username;

    /**
     * Default constructor
     * <p>
     * When created, a user is associated to a virtualClient, which handles all its requests
     *
     * @param virtualClient the virtualClient associated to the user
     * @param server        the server reference
     */
    public User(VirtualClient virtualClient, Server server) {
        this.virtualClient = virtualClient;
        this.server = server;
    }

    /**
     * <i>username</i> getter
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * <i>username</i> setter
     *
     * @param username the user's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sends a message to the client
     *
     * @param message the message to send
     */
    public void notify(Message message) {
        virtualClient.notify(message);
    }

    /**
     * Ends the connection between the server and client
     */
    public void closeConnection() {
        virtualClient.closeConnection();
    }

    /**
     * Provides the lobby in which the user is currently in
     *
     * @return the Lobby object where this user is contained
     */
    public Lobby getRoom() {
        return server.getUsers().get(this);
    }

}
