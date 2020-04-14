package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.request.MessageRequest;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VirtualClient extends Thread{
    private String username;

    private ObjectInputStream ois;  // Object o Data ??
    private ObjectOutputStream oos;  // Object o Data ??

    /*

    private Socket clientConnection;
    private final SocketServer server;
    private MessageParser messageParser;
    private static Logger logger = Logger.getLogger("virtualClient");

    public VirtualClient(SocketServer server, Socket clientConnection) {
        this.server = server;
        this.clientConnection = clientConnection;

        try {
            this.ois = new ObjectInputStream(this.clientConnection.getInputStream());
            // otherwise: BufferedReader in = new BufferedReader(new ObjectInputStream(socket.getInputStream()));
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        }

        try{
            this.oos = new ObjectOutputStream(this.clientConnection.getOutputStream());
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
                MessageRequest message = (MessageRequest) ois.readObject();
                if (message == null) {
                    loop = false;
                } else {
                    if(message.content == ((LoginRequest) message).content.LOGIN) {
                    try{
                        this.username = message.username;
                        server.addClient(this);
                    } catch(Exception e) {
                         logger.log(Level.WARNING,e.getMessage());
                         this.clientConnection.close();
                    }
                    }
                    else if(client is in the game) {
                        server.handleMessage(message);
                    }
                    else{
                        close connection from unknown client
                    }
                }
            }
        } catch (IOException|ClassNotFoundException e) {
            ???  server.onDisconnect(username);
            e.printStackTrace();
        }
    }

     */

    public String getUsername() {
        return username;
    }

    public void notify(MessageResponse messageResponse) throws IOException {
        oos.reset();
        oos.writeObject(messageResponse);
        oos.flush();
    }

    public void notify(MessageRequest messageRequest) throws IOException {
        oos.reset();
        oos.writeObject(messageRequest);
        oos.flush();
    }
}
