package it.polimi.ingsw.network.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.Client.Client;
import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.LoginRequest;
import it.polimi.ingsw.network.message.response.fromServerToClient.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class ServerTest {
    private Client client1, client2, client3;
    private final Logger logger = Logger.getLogger("ServerLogger");

    @BeforeEach
    void setup(){
        try {
            Server server = new Server();
            server.startServer();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }



        client1 = new Client("user1", "127.0.0.1");
        client2 = new Client("user2", "127.0.0.1");
        client3 = new Client("user3", "127.0.0.1");
    }


    @Test
    void client1Connection() throws IOException, InterruptedException {
        client1.startConnection();
        while (true);
    }


}