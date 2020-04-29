package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.CLI;

import java.io.IOException;
import java.util.List;

public class Client {
    private final ViewInterface view;
    private String username;
    private String ipAddress;
    private NetworkHandler networkHandler;
    private boolean currentPlayer;

    public Client(String username, String ipAddress, ViewInterface viewInterface) {
        this.view = viewInterface;
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public static void main(String[] args) {
        ViewInterface viewInterface;
        if (args.length == 0 || !args[0].equals("--GUI"))
            viewInterface = new CLI();
        else
            viewInterface = new CLI(); //FIXME: implement gui

        List<String> loginData = viewInterface.loginScreen();
        Client client = new Client(loginData.get(1), loginData.get(0), viewInterface);
        client.startConnection();
        //TODO: Here I ask the user if he wants to use the Cli/Gui
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        networkHandler.login(username);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /*
        The view asks the user for his Username and IpAddress (we need this because of quarantine), then it'll call this method to start the connection
         */
    public void startConnection() {
        networkHandler = new NetworkHandler(this);
        new Thread(networkHandler).start();
        networkHandler.login(this.username);
    }

    public void sendMessage(Message message) { //View -> Client -> handler -> JsonParser -> VirtualClient -> Server
        if (currentPlayer)
            networkHandler.sendMessage(message);
    }


    public void stopConnection() throws IOException {
        networkHandler.closeConnection();
    }

    public ViewInterface getView() {
        return this.view;
    }

}

