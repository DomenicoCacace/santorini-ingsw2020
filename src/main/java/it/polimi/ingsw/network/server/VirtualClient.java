package it.polimi.ingsw.network.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.LoginRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualClient extends Thread {
    private final Server server;
    private final Socket clientConnection;
    private String username;
    private static final Logger logger = Logger.getLogger("virtualClient");
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;
    private final JacksonMessageBuilder jsonParser;

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
            boolean loop = true;
            while (loop) {
                //When a message is received, forward to Server
                String input = inputSocket.readLine();
                System.out.println("message received" + input);
                Message message = jsonParser.fromStringToMessage(input);
                if (message == null) {
                    loop = false;
                } else {
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
}
