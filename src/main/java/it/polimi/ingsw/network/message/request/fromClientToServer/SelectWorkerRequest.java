package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;

public class SelectWorkerRequest extends MessageFromClientToServer {
    private final Worker targetWorker;

    @JsonCreator
    public SelectWorkerRequest(@JsonProperty("username") String username, @JsonProperty("targetWorker") Worker targetWorker) {
        super(username, Content.SELECT_WORKER);
        this.targetWorker = targetWorker;
    }

    public Worker getTargetWorker() {
        return targetWorker;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.selectWorker(this);
    }
}
