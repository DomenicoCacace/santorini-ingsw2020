package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualClient extends Thread {
    private static final Logger logger = Logger.getLogger("virtualClient");
    private final Server server;
    private final Socket clientConnection;
    private final JacksonMessageBuilder jsonParser;
    private String username;
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;

    public VirtualClient(Server server, Socket clientConnection) {
        this.server = server;
        this.clientConnection = clientConnection;
        try {
            inputSocket = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        try {
            outputSocket = new OutputStreamWriter(clientConnection.getOutputStream());
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        jsonParser = new JacksonMessageBuilder();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message;
                //When a message is received, forward to Server
                String input = inputSocket.readLine();
                System.out.println("message received " + input);
                try {
                    message = jsonParser.fromStringToMessage(input);
                } catch (NullPointerException e) {
                    server.onDisconnect(username);
                    break;
                }

                if (message.getContent() == Message.Content.LOGIN) {
                    try {
                        this.username = message.getUsername();
                        server.addClient(this);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, e.getMessage());
                        this.clientConnection.close();
                    }
                } else if (server.containClient(username)) {
                    server.handleMessage(message);
                } else {
                    try {
                        clientConnection.close();
                        server.onDisconnect(username);
                    } catch (IOException e) {
                        //Do nothing, connection already closed
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            server.onDisconnect(username);
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void notify(Message message) {
        String stringMessage = jsonParser.fromMessageToString(message);
        try {
            outputSocket.write(stringMessage + "\n");
            outputSocket.flush();
            //To debug
            System.out.println(stringMessage + "Message sent from server to: " + username);
            //
        } catch (IOException e) {
            try {
                clientConnection.close();
            } catch (IOException e2) {
                //Do nothing
            }
            server.onDisconnect(username);
        }
    }

    public void closeConnection() {
        try {
            clientConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
