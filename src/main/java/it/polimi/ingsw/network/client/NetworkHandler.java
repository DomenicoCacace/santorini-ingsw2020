package it.polimi.ingsw.network.client;


import it.polimi.ingsw.network.message.*;
import it.polimi.ingsw.network.message.request.fromClientToServer.LoginRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class NetworkHandler implements Runnable {
    private final static String PING = "ping";
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;
    private Socket serverConnection;    //removed openConnection boolean
    private final JacksonMessageBuilder jacksonParser;
    private final Client client;
    private final MessageManagerParser parser;
    private ScheduledThreadPoolExecutor ex;
    private final BlockingQueue<MessageFromServerToClient> queue = new ArrayBlockingQueue<>(15);


    public NetworkHandler(Client client) {
        this.jacksonParser = new JacksonMessageBuilder();
        this.client = client;
        this.parser = new MessageManagerParser(client);
        try {
            this.serverConnection = new Socket(client.getIpAddress(), 4321);    //FIXME: hardcoded port
            this.inputSocket = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
            this.outputSocket = new OutputStreamWriter(serverConnection.getOutputStream());
            this.outputSocket.flush();
        } catch (IOException e) {
            closeConnection();
        }
    }

    @Override
    public void run() {
        new Thread(() -> {
            try {
                while (true)
                    queue.take().callVisitor(parser);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        while (true) {
            String ioData = "";
            try {
                ioData = inputSocket.readLine();
                if (ioData == null) {
                    client.getView().showErrorMessage("You have been disconnected");
                    break;
                }
                if (ioData.equals(PING))
                    new Thread(this::ping).start();
                else {
                    Message message;
                    message = jacksonParser.fromStringToMessage(ioData);
                    queue.put((MessageFromServerToClient) message);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        closeConnection();
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
        try {
            outputSocket.close();
            inputSocket.close();
            serverConnection.close();
        } catch (Exception e) {
            client.getView().showErrorMessage("Couldn't connect to the server with IP address: " + client.getIpAddress() + "!\n");
        }
    }

    public synchronized void sendMessage(Message message) throws IOException {
        String json = jacksonParser.fromMessageToString(message);
        if (!serverConnection.isClosed()) {
            outputSocket.write(json + "\n");
            outputSocket.flush();
            //System.out.println(json + "message sent from " + client.getUsername() + " to Server");
        } else throw new IOException();
    }

    public synchronized void sendMessage(String string) throws IOException {
        if (!serverConnection.isClosed()) {
            outputSocket.write(string + "\n");
            outputSocket.flush();
            //System.out.println(json + "message sent from " + client.getUsername() + " to Server");
        } else throw new IOException();
    }

    public void ping() {
        if (ex != null)
            ex.shutdownNow();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            sendMessage(PING);
        } catch (IOException e) {
            e.printStackTrace();
            Client.initClient(client.getView());
            return;
        }
        ex = new ScheduledThreadPoolExecutor(5);
        ex.schedule(() -> {
            System.out.println("Timeout!");
            Client.initClient(client.getView());
        }, 5, TimeUnit.SECONDS);
    }
}



