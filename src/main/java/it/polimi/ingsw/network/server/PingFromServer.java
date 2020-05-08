package it.polimi.ingsw.network.server;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PingFromServer implements Runnable {
    private final static Logger logger = Logger.getLogger(Logger.class.getName());
    private final VirtualClient virtualClient;


    public PingFromServer(VirtualClient client) {
        this.virtualClient = client;
    }

    @Override
    public void run() {
        ExecutorService exe = Executors.newSingleThreadExecutor();
        Future<Integer> future = exe.submit(virtualClient::ping);
        while (true){
            try {
                future.get(3000, TimeUnit.MILLISECONDS);
                Thread.sleep(100);
                future = exe.submit(virtualClient::ping);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.log(Level.SEVERE, ("Pong not received : "), e);
                virtualClient.disconnectFromServer();
                break;
            }
        }
    }
}
