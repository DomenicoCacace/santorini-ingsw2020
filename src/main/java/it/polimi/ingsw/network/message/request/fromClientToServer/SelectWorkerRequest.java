package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.model.Worker;

public class SelectWorkerRequest extends MessageFromClientToServer {
    private final Worker targetWorker;

    @JsonCreator
    public SelectWorkerRequest(@JsonProperty("username") String username, @JsonProperty("targetWorker") Worker targetWorker) {
        super(username, Type.CLIENT_REQUEST);
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
