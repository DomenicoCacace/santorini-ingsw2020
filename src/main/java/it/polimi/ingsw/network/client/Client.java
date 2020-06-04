package it.polimi.ingsw.network.client;

import com.fasterxml.jackson.databind.node.NumericNode;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.gui.GUI;
import it.polimi.ingsw.view.inputManagers.LoginManager;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Representation of a client,
 */
public class Client {
    private static final int MAX_SETTINGS_STORED = 5;
    private static final File CONFIG_FILE = new File("../config.txt");

    private final ViewInterface view;
    private String username;
    private String ipAddress;
    private NetworkHandler networkHandler;
    private boolean currentPlayer;


    public Client(ViewInterface viewInterface){
        this.view = viewInterface;
    }

    /**
     * Default constructor
     *
     * @param username      the client's username
     * @param ipAddress     the server address
     */
    public Client(ViewInterface viewInterface, String username, String ipAddress) {
        this.view = viewInterface;
        this.username = username;
        this.ipAddress = ipAddress;
        this.currentPlayer = true;
    }

    /**
     * Launches the client
     * <p>
     * To determine which UI to use, this method uses command line arguments
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ViewInterface view;
        try {
            if (args.length == 0 || !args[0].equals("--CLI")) {
                GUI.launchGui();
            } else {
                view = new CLI();
                initClient(view);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not start the game, exiting...");
            System.exit(-1);
        }
    }

    /**
     * Creates a new client instance
     *
     * @param viewInterface the UI to use
     */
    public static void
    initClient(ViewInterface viewInterface) {
        Client client = new Client(viewInterface);
        viewInterface.printLogo();
        List<String> savedUsers = new ArrayList<>();
        try {
            if (!CONFIG_FILE.createNewFile()) {
                FileReader fileReader = new FileReader(CONFIG_FILE);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    savedUsers.add(line);
                    savedUsers.add(bufferedReader.readLine());
                }
                bufferedReader.close();
                viewInterface.setInputManager(new LoginManager(client, savedUsers));
                viewInterface.askToReloadLastSettings(savedUsers);
            } else { //the file is empty
                viewInterface.setInputManager(new LoginManager(client, savedUsers));
                viewInterface.askIP();
            }

        } catch (IOException e) {
            Client.initClient(viewInterface);
            e.printStackTrace();
        }
    }

    /**
     * Saves a new login configuration to a file
     *
     * @param ip       the server address
     * @param username the username
     * @throws IOException if an I/O error occurs
     */
    public void writeSettingsToFile(String ip, String username) throws IOException {
        StringBuilder otherUsers = new StringBuilder();
        String ipLine;
        String nameLine;
        int storedSetting = 0;
        FileReader fileReader = new FileReader(CONFIG_FILE);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (((ipLine = bufferedReader.readLine()) != null && !ipLine.equals("")) && storedSetting < MAX_SETTINGS_STORED - 1) {
            if (!(nameLine = bufferedReader.readLine()).equals(username) || !ipLine.equals(ip)) {
                otherUsers.append(ipLine).append("\n").append(nameLine).append("\n");
                storedSetting++;
            }
        }
        String builder = ip + "\n" + username + "\n";
        FileWriter fileWriter = new FileWriter(CONFIG_FILE);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(builder);
        bufferedWriter.append(otherUsers.toString());
        bufferedWriter.close();
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
     * Sets the username and, if possible, starts a connection
     *
     * @param username the chosen username
     * @see #startConnection()
     */
    public void setUsername(String username) {
        this.username = username;
        if (networkHandler == null)
            startConnection();
        else
            networkHandler.login(username);
    }

    /**
     * <i>ipAddress</i> getter
     *
     * @return the server address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * <i>ipAddress</i> setter
     *
     * @param ipAddress the server address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Opens a connection to the server
     */
    private void startConnection() {
        try {
            networkHandler = new NetworkHandler(this);
            setCurrentPlayer(true);
            networkHandler.login(this.username);
            new Thread(networkHandler).start();
        } catch (IOException | NumberFormatException | InterruptedException | ExecutionException | TimeoutException e) {
            view.showErrorMessage("Couldn't connect to ip address: " + ipAddress);
            Client.initClient(view);
        }
    }

    /**
     * Sets a flag allowing the user to send messages
     *
     * @param currentPlayer a boolean value
     */
    public void setCurrentPlayer(boolean currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Sends a message to the server
     *
     * @param message the message to be sent
     */
    public void sendMessage(Message message) { //View -> Client -> handler -> JsonParser -> VirtualClient -> Server
        if (currentPlayer) {
            try {
                networkHandler.sendMessage(message);
            } catch (IOException e) {
                networkHandler.closeConnection();
            }
        }
    }

    /**
     * Terminates the connection with the server, closing the socket streams
     */
    public void stopConnection() {
        networkHandler.closeConnection();
    }

    public ViewInterface getView() {
        return view;
    }

    public void inputTimeout() {
        stopConnection();
        Client.initClient(view);
    }
}