package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.request.MessageRequest;

public class SelectWorkerRequest extends MessageRequest {
    public final Worker targetWorker;

    public SelectWorkerRequest(String username, Worker targetWorker) {
        super(username, MessageRequest.Content.SELECT_WORKER);
        this.targetWorker=targetWorker;
    }
}
