package it.polimi.ingsw.network.client;


import com.fasterxml.jackson.databind.node.NumericNode;
import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.fromClientToServer.LoginRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;

/**
 * Client-side client communication and message handler
 */
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


    /**
     * Default constructor
     * <p>
     * Tries to open a socket channel with the server
     *
     * @param client the client to connect
     * @throws IOException if an I/O error occurs while opening the socket streams
     */
    public NetworkHandler(Client client) throws IOException, NumberFormatException, InterruptedException, ExecutionException, TimeoutException {
        this.jacksonParser = new JacksonMessageBuilder();
        this.client = client;
        this.parser = new MessageManagerParser(client);
        //TODO: add timeout
        String address;
        int port;
        String clientInput = client.getIpAddress();
        if (clientInput.contains(":")) {
            int index = clientInput.indexOf(":");
            address = clientInput.substring(0, index).trim();
            port = Integer.parseInt(clientInput.substring(index + 1).trim());
        } else {
            address = clientInput;
            port = 4321;
        }
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Socket> socketFuture = ex.submit(() -> new Socket(address, port));
        this.serverConnection = socketFuture.get(5, TimeUnit.SECONDS);
        this.inputSocket = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
        this.outputSocket = new OutputStreamWriter(serverConnection.getOutputStream());
        this.outputSocket.flush();
    }

    /**
     * Keeps listening on the network for messages
     * <p>
     * In case of a ping message, see {@linkplain #ping()}; for any other message, the relative visitor method is
     * called
     *
     * @see ClientMessageManagerVisitor
     */
    @Override
    public void run() {
        while (true) {
            String ioData = "";
            try {
                ioData = inputSocket.readLine();
                if (ioData == null) {
                    client.getView().showErrorMessage("You have been disconnected");
                    break;
                } else if (ioData.equals(PING))
                    new Thread(this::ping).start();
                else {
                    MessageFromServerToClient message;
                    message = (MessageFromServerToClient) jacksonParser.fromStringToMessage(ioData);
                    message.callVisitor(parser);
                }
            } catch (IOException e) {
                return;
            }
        }
    }

    /**
     * Sends a login request to the server
     *
     * @param username the username to log the user in
     */
    public void login(String username) {
        try {
            sendMessage(new LoginRequest(username));
            client.getView().showSuccessMessage("Login request sent, waiting for a response...");
        } catch (IOException e) {
            closeConnection();
        }

    }

    /**
     * Terminates the current socket connection
     * <p>
     * This method causes also the input and output socket streams to be closed
     */
    public void closeConnection() {
        try {
            outputSocket.close();
            inputSocket.close();
            serverConnection.close();
            ex.shutdown();
            pingTask.cancel(true);
        } catch (Exception e) {
            //
        }
    }

    /**
     * Sends a message to the server
     *
     * @param message the {@linkplain Message} to be sent
     * @throws IOException if an I/O error occurs
     */
    public synchronized void sendMessage(Message message) throws IOException {
        String json = jacksonParser.fromMessageToString(message);
        if (!serverConnection.isClosed()) {
            outputSocket.write(json + "\n");
            outputSocket.flush();
            //System.out.println(json + "message sent from " + client.getUsername() + " to Server");
        } else throw new IOException();
    }

    /**
     * Sends a string to the server
     *
     * @param string the string to send
     * @throws IOException if an I/O error occurs
     * @see #ping()
     */
    public synchronized void sendMessage(String string) throws IOException {
        if (!serverConnection.isClosed()) {
            outputSocket.write(string + "\n");
            outputSocket.flush();
            //System.out.println(json + "message sent from " + client.getUsername() + " to Server");
        } else throw new IOException();
    }

    /**
     * Handles the client-server ping messages to check for connectivity
     * <p>
     * Every 5 seconds, this methods schedules a {@linkplain #closeConnection()} to be run; when a ping message
     * is received, the timer is defused and the thread is put to sleep for a second.
     * <br>
     * At every call, a ping message is also sent to the server
     */
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
        if (!serverConnection.isClosed()) {
            ex = new ScheduledThreadPoolExecutor(5);
            ex.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            ex.setRemoveOnCancelPolicy(true);
            pingTask = ex.schedule(() -> {
                closeConnection();
                client.getView().showErrorMessage("The Server crashed!!");
                new Thread(() -> Client.initClient(client.getView())).start();
            }, 5, TimeUnit.SECONDS);
        }
    }
}



