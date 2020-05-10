package it.polimi.ingsw.network.client;

import java.util.concurrent.*;
import java.util.logging.Level;

public class PingFromClient implements Runnable{
    private final NetworkHandler networkHandler;

    public PingFromClient(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    @Override
    public void run() {
        ExecutorService exe = Executors.newSingleThreadExecutor();
        Future<Integer> future = exe.submit(networkHandler::ping);
        while (true){
            try {
                future.get(3000, TimeUnit.MILLISECONDS);
                Thread.sleep(100);
                future = exe.submit(networkHandler::ping);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                networkHandler.initClient();
                break;
            }
        }

    }
}
