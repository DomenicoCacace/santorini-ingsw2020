package it.polimi.ingsw.network.Client;

import it.polimi.ingsw.network.message.Message;

import java.io.IOException;

public class Client{

    private String username;
    private String ipAddress;
    private NetworkHandler networkHandler;
    private boolean currentPlayer;

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public static void main(String[] args) {
        //TODO: Here I ask the user if he wants to use the Cli/Gui
    }


    /*
    The view asks the user for his Username and IpAddress (we need this because of quarantine), then it'll call this method to start the connection
     */
    public void startConnection(String username, String ipAddress) throws IOException {
        this.username = username;
        this.ipAddress = ipAddress;
        networkHandler = new NetworkHandler(this);
        new Thread(networkHandler).start();
        networkHandler.login(this.username);
    }

    public void sendMessage(Message message) throws IOException { //View -> Client -> handler -> JsonParser -> VirtualClient -> Server
        if(currentPlayer)
            networkHandler.sendMessage(message);
    }

    public void stopConnection() throws IOException {
        networkHandler.closeConnection();
    }

    public void notify(Message message){ //These come from the server to the Client
        System.out.println("Message Received");
        //TODO: We could have another message parser class instead of a method, here the client will call methods of the view to display the info inside the message (errors, gameboard, etc)
    }
}

