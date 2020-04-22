package it.polimi.ingsw.network.Client;

import it.polimi.ingsw.network.message.Message;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    private String username;
    private final String ipAddress;
    private NetworkHandler networkHandler;
    private boolean currentPlayer;

    public static void main(String[] args) throws IOException {
        System.out.println("Choose username");
        Scanner in = new Scanner(System.in);
        String username = in.nextLine();
        System.out.println("IP");
        String ip = in.nextLine();
        Client client = new Client(username, ip);
        client.startConnection();
        //TODO: Here I ask the user if he wants to use the Cli/Gui
    }

    public Client(String username, String ipAddress) {
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /*
        The view asks the user for his Username and IpAddress (we need this because of quarantine), then it'll call this method to start the connection
         */
    public void startConnection() throws IOException {
        networkHandler = new NetworkHandler(this);
        new Thread(networkHandler).start();
        networkHandler.login(this.username);
    }

    public void sendMessage(Message message) throws IOException { //View -> Client -> handler -> JsonParser -> VirtualClient -> Server
        if (currentPlayer)
            networkHandler.sendMessage(message);
    }

    public void setUsername(String username) {
        this.username = username;
        networkHandler.login(username);
    }

    public void stopConnection() throws IOException {
        networkHandler.closeConnection();
    }

    public synchronized void notify(Message message) { //These come from the server to the Client
        System.out.println("Message Received from server, message content is " + message.getContent());
        //TODO: We could have another message parser class instead of a method, here the client will call methods of the view to display the info inside the message (errors, gameboard, etc)
    }
}

