package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.gui.GUI;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;

public class Client {
    private static final File CONFIG_FILE = new File("../config.txt");
    private static final int MAX_SETTINGS_STORED = 5;
    private final ViewInterface view;
    private String username;
    private String ipAddress;
    private static NetworkHandler networkHandler;
    private boolean currentPlayer;


    public Client(String username, String ipAddress, ViewInterface viewInterface) {
        this.view = viewInterface;
        this.username = username;
        this.ipAddress = ipAddress;
        this.currentPlayer = true;
    }

    public static void main(String[] args) {
        ViewInterface viewInterface;
        try {
            if (args.length == 0 || !args[0].equals("--GUI")) {
                viewInterface = new CLI();
                initClient(viewInterface);
            }
            else
                GUI.launchGui(); //FIXME
        } catch (IOException e) {
            System.out.println("Error: resources not found");
            System.exit(1);
        }
        //TODO: Here I ask the user if he wants to use the Cli/Gui
    }

    public static synchronized void initClient(ViewInterface viewInterface){
        List<String> loginData = new ArrayList<>();
        if (networkHandler !=null)
            networkHandler.closeConnection();
        viewInterface.printLogo();
        try {
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
        } catch (IOException | TimeoutException | InterruptedException e) {
            viewInterface.showErrorMessage("Timeout!!");
            viewInterface.stopInput();
            Client.initClient(viewInterface);
            e.printStackTrace();
            return;
        } catch (CancellationException e){
            return;
        }
        new Client(loginData.get(1), loginData.get(0), viewInterface).startConnection();
    }

    public void writeSettingsToFile(String ip, String username) throws IOException {
        StringBuilder otherUsers = new StringBuilder();
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
        try{
            networkHandler = new NetworkHandler(this);
            networkHandler.login(this.username);
            new Thread(networkHandler).start();
        }catch (IOException e) {
            view.showErrorMessage("Couldn't connect to ip address: " + ipAddress);
            initClient(view);
        }
    }

    public void sendMessage(Message message) { //View -> Client -> handler -> JsonParser -> VirtualClient -> Server
        if (currentPlayer) {
            try {
                networkHandler.sendMessage(message);
            } catch (IOException e) {
                networkHandler.closeConnection();
            }
        }
}


    public void stopConnection() {
        networkHandler.closeConnection();
    }

    public ViewInterface getView() {
        return this.view;
    }

}