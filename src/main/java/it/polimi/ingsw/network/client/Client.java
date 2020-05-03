package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.CLI;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final File CONFIG_FILE = new File("../config.txt");
    private static final int MAX_SETTINGS_STORED = 5;
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

    public static void main(String[] args) throws IOException {
        ViewInterface viewInterface;
        if (args.length == 0 || !args[0].equals("--GUI"))
            viewInterface = new CLI();
        else
            viewInterface = new CLI(); //FIXME: implement gui
        List<String> loginData = new ArrayList<>();
        viewInterface.printLogo();
        if (!CONFIG_FILE.createNewFile()) {
            FileReader fileReader = new FileReader(CONFIG_FILE);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            List<String> savedUsers = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                savedUsers.add(line);
                savedUsers.add(bufferedReader.readLine());
            }
            bufferedReader.close();
            if ((loginData = viewInterface.askToReloadLastSettings(savedUsers)).size()==0) {
                loginData.add(viewInterface.askIP());
                loginData.add(viewInterface.askUsername());
            }
        } else { //If the file is empty
            loginData.add(viewInterface.askIP());
            loginData.add(viewInterface.askUsername());
        }
        Client client = new Client(loginData.get(1), loginData.get(0), viewInterface);
        client.startConnection();
        //TODO: Here I ask the user if he wants to use the Cli/Gui
    }

    public void writeSettingsToFile(String ip, String username) throws IOException {
        StringBuilder otherUsers = new StringBuilder("");
        String ipLine;
        String nameLine;
        int storedSetting=0;
        FileReader fileReader = new FileReader(CONFIG_FILE);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (((ipLine = bufferedReader.readLine()) != null && !ipLine.equals("")) && storedSetting < MAX_SETTINGS_STORED -1){
            if(!(nameLine = bufferedReader.readLine()).equals(username) || !ipLine.equals(ip)) {
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


    public void stopConnection() {
        networkHandler.closeConnection();
    }

    public ViewInterface getView() {
        return this.view;
    }

}

