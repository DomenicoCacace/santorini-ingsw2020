package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class WorkerSelectedEvent extends MessageFromServerToClient {
    private final List<PossibleActions> possibleActions;
    private final Worker selectedWorker;

    @JsonCreator
    public WorkerSelectedEvent(@JsonProperty("type") Type type, @JsonProperty("username") String username,
                               @JsonProperty("possible actions") List<PossibleActions> possibleActions, @JsonProperty("SelectedWorker") Worker selectedWorker) {
        super(username, type);

        if (type.equals(Type.OK)) {
            this.possibleActions = possibleActions;
            this.selectedWorker = selectedWorker;
        } else {
            this.possibleActions = null;
            this.selectedWorker = null;
        }
    }

    public List<PossibleActions> getPossibleActions() {
        return possibleActions;
    }

    public Worker getSelectedWorker() {
        return selectedWorker;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onWorkerSelected(this);
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
