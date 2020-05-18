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
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class NetworkHandler implements Runnable {
    private final static String PING = "ping";
    private final BufferedReader inputSocket;
    private final OutputStreamWriter outputSocket;
    private final Socket serverConnection;
    private final JacksonMessageBuilder jacksonParser;
    private final Client client;
    private final MessageManagerParser parser;
    private ScheduledThreadPoolExecutor ex;
    private ScheduledFuture<?> pingTask;
    private final BlockingQueue<MessageFromServerToClient> queue = new ArrayBlockingQueue<>(15);


    public NetworkHandler(Client client) throws IOException {
        this.jacksonParser = new JacksonMessageBuilder();
        this.client = client;
        this.parser = new MessageManagerParser(client);
        try {
            //TODO: add timeout
            this.serverConnection = new Socket(client.getIpAddress(), 4321);    //FIXME: hardcoded port
        } catch (UnknownHostException e){
            throw new UnknownHostException();
        }
        this.inputSocket = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
        this.outputSocket = new OutputStreamWriter(serverConnection.getOutputStream());
        this.outputSocket.flush();
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
                else if (ioData.equals(PING))
                    new Thread(this::ping).start();
                else {
                    MessageFromServerToClient message;
                    message = (MessageFromServerToClient) jacksonParser.fromStringToMessage(ioData);
                    if(message.isBlocking())
                        queue.put(message);
                    else
                        message.callVisitor(parser);
                }
            } catch (IOException | InterruptedException e) {
                return;
            }
        }
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
            ex.shutdown();
            pingTask.cancel(true);

            queue.clear();
        } catch (Exception e) {
            //
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
        if (ex != null) {
            ex.shutdownNow();
            pingTask.cancel(true);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            sendMessage(PING);
        } catch (IOException e) {
            //TODO:
        }
        if(!serverConnection.isClosed()) {
            ex = new ScheduledThreadPoolExecutor(5);
            ex.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            ex.setRemoveOnCancelPolicy(true);
            pingTask = ex.schedule(() -> {
                queue.clear();
                client.getView().stopInput();
                closeConnection();
                client.getView().showErrorMessage("The Server crashed!!");
                new Thread(() -> Client.initClient(client.getView())).start();
            }, 5, TimeUnit.SECONDS);
        }
    }
}



