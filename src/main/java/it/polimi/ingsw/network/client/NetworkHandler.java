package it.polimi.ingsw.network.Client;


import it.polimi.ingsw.network.message.*;
import it.polimi.ingsw.network.message.request.fromClientToServer.LoginRequest;


import java.io.*;
import java.net.Socket;

public class NetworkHandler implements Runnable {
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;
    private Socket socketClient;
    private boolean openConnection;
    private JacksonMessageBuilder jacksonParser;
    private Client client;


    public BufferedReader getInputSocket() {
        return inputSocket;
    }
    public OutputStreamWriter getOutputSocket() {
        return outputSocket;
    }

    public NetworkHandler(Client client) {
        try {
            this.client = client;
            this.socketClient = new Socket(client.getIpAddress(), 4321);
            this.inputSocket = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            this.outputSocket = new OutputStreamWriter(socketClient.getOutputStream());
            this.outputSocket.flush();
            this.openConnection = true;
        } catch (IOException e) {
            this.openConnection = false;
        }
    }

    @Override
    public void run() {
        while (openConnection) {
            String ioData;

            try {
                ioData = inputSocket.readLine();
            } catch (IOException e) {
                break;
            }

            if (ioData == null) {
                openConnection = false;
                closeConnection();
                break;
            }
            Message message = jacksonParser.fromStringToMessage(ioData);
            client.notify(message);
        }
        System.out.println("You have been disconnected");
    }

    public void login(String username) throws IOException{
        sendMessage(new LoginRequest(username));
    }


    public void closeConnection()  {
        openConnection = false;
        try {
            outputSocket.close();
            inputSocket.close();
            socketClient.close();
        }
        catch (Exception e){
            e.printStackTrace();
            //Todo that means that the connection was already closed
        }
    }

    public void sendMessage(Message message) throws IOException {
        String json = jacksonParser.fromMessageToString(message);
        try {
            outputSocket.write(json + "\n");
            outputSocket.flush();
        } catch (Exception e) {
            closeConnection();
        }
    }

}

