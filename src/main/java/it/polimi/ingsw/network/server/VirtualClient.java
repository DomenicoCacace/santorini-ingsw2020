package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.request.MessageRequest;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualClient extends Thread{
    private final Server server;
    private final Socket clientConnection;
    private String username;
    private static final Logger logger = Logger.getLogger("virtualClient");
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;
    private JacksonMessageBuilder jsonParser;
    public VirtualClient(Server server, Socket clientConnection) {
        this.server = server;
        this.clientConnection = clientConnection;
        try {
            inputSocket = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
            // otherwise: BufferedReader in = new BufferedReader(new ObjectInputStream(socket.getInputStream()));
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        }
        try{
            outputSocket = new OutputStreamWriter(clientConnection.getOutputStream());
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        }
    }
    @Override
    public void run() {
        try {
            boolean loop = true;
            while(loop) {
                //When a message is received, forward to Server
                MessageRequest message = jsonParser.fromStringToRequest(inputSocket.readLine());
                if (message == null) {
                    loop = false;
                } else {
                    if(message.content == MessageRequest.Content.LOGIN) {
                        try {
                            this.username = message.username;
                            server.addClient(this);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, e.getMessage());
                            this.clientConnection.close();
                        }
                    }
                    //else if(server.containClient(username)) {
                     //   server.handleMessage(message); // TODO: check if message is request or response
                    // }
                    else{
                        clientConnection.close();
                        server.onDisconnect(username);
                    }
                }
            }
        } catch (IOException /* | InterruptedException*/ e) {
            server.onDisconnect(username);
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
    public void notify(MessageResponse messageResponse) throws IOException {
        String stringMessage = jsonParser.fromResponseToString(messageResponse);
        try {
            outputSocket.write(stringMessage + "\n");
            outputSocket.flush();
        } catch (IOException e) {
            clientConnection.close();
            server.onDisconnect(username);
            throw new IOException();
        }
    }
    public void notify(MessageRequest messageRequest) throws IOException {
        String stringMessage = jsonParser.fromRequestToString(messageRequest);
        try {
            outputSocket.write(stringMessage + "\n");
            outputSocket.flush();
        } catch (IOException e) {
            clientConnection.close();
            server.onDisconnect(username);
            throw new IOException();
        }
    }
}

