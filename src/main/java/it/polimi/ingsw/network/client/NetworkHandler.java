package it.polimi.ingsw.network.client;


import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.request.fromClientToServer.LoginRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class NetworkHandler implements Runnable {
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;
    private Socket socketClient;
    private boolean openConnection;
    private JacksonMessageBuilder jacksonParser;
    private Client client;
    private MessageManagerParser parser;

    public NetworkHandler(Client client) {
        this.jacksonParser = new JacksonMessageBuilder();
        this.client = client;
        this.openConnection = true;
        this.parser = new MessageManagerParser(client);
        try {
            this.socketClient = new Socket(client.getIpAddress(), 4321);    //FIXME: hardcoded port
            this.inputSocket = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            this.outputSocket = new OutputStreamWriter(socketClient.getOutputStream());
            this.outputSocket.flush();
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
                //System.out.println(ioData);
            } catch (IOException e) {
                System.out.println("IOException");
                break;
            }
            if (ioData == null) {
                openConnection = false;
                client.getView().showErrorMessage("You have been disconnected");
                break;
            }
            Message message;
            try {
                message = jacksonParser.fromStringToMessage(ioData);
                ((MessageFromServerToClient)message).callVisitor(parser);  //TODO: In both server and client we need to cast the input Message in MessageFromServerToclient or MessageFromClientToServer in order to use callVisitor method
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Client.initClient(client.getView());
    }

    public void login(String username) {
        try {
            sendMessage(new LoginRequest(username));
            client.getView().showSuccessMessage("Login request sent, waiting for a response...");
        } catch (IOException e) {
            closeConnection();
        }

    }


    public void closeConnection() {
        openConnection = false;
        try {
            outputSocket.close();
            inputSocket.close();
            socketClient.close();
        } catch (Exception e) {
            client.getView().showErrorMessage("Couldn't connect to the server with IP address: " + client.getIpAddress() + "!\n");
        }
    }

    public synchronized void sendMessage(Message message) throws IOException {
        String json = jacksonParser.fromMessageToString(message);
        if(openConnection) {
            outputSocket.write(json + "\n");
            outputSocket.flush();
            System.out.println(json + "message sent from " + client.getUsername() + " to Server");
        }
        else throw new IOException();
    }
}

