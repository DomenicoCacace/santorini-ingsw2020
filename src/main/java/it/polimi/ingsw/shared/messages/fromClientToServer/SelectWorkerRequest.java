package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Worker;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

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
