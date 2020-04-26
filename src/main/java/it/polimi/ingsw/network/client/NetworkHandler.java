package it.polimi.ingsw.network.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;
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
    private MessageParser parser;

    public NetworkHandler(Client client) {
        try {
            this.client = client;
            this.jacksonParser = new JacksonMessageBuilder();
            this.socketClient = new Socket(client.getIpAddress(), 4321);
            System.out.println(socketClient.toString() + " client");
            this.inputSocket = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            this.outputSocket = new OutputStreamWriter(socketClient.getOutputStream());
            this.outputSocket.flush();
            this.openConnection = true;
            this.parser = new MessageParser(client);
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
                System.out.println(ioData);
            } catch (IOException e) {
                System.out.println("IOEXception");
                break;
            }

            if (ioData == null) {
                openConnection = false;
                System.out.println("null");
                closeConnection();
                break;
            }

            Message message;
            try {
                message = jacksonParser.fromStringToMessage(ioData);
                parser.parseMessageFromServerToClient(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("You have been disconnected");
    }

    public void login(String username) {
        sendMessage(new LoginRequest(username));
        System.out.println("Login sent");
    }


    public void closeConnection() {
        openConnection = false;
        try {
            outputSocket.close();
            inputSocket.close();
            socketClient.close();
        } catch (Exception e) {
            System.out.println("The connection was already closed!\n");
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(Message message) {
        String json = jacksonParser.fromMessageToString(message);
        try {
            outputSocket.write(json + "\n");
            outputSocket.flush();
            //To Debug
            System.out.println(json + "message sent from " + client.getUsername() +" to Server");
            //
        } catch (Exception e) {
            closeConnection();
        }
    }

}

