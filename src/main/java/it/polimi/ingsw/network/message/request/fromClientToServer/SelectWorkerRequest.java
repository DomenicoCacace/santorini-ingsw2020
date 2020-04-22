package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;

public class SelectWorkerRequest extends Message {
    private final Worker targetWorker;


    public SelectWorkerRequest(String username, Worker targetWorker) {
        super(username, Content.SELECT_WORKER);
        this.targetWorker = targetWorker;
    }

    public Worker getTargetWorker() {
        return targetWorker;
    }
}
