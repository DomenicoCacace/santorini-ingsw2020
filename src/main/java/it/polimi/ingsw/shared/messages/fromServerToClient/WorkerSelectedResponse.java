package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.PossibleActions;
import it.polimi.ingsw.shared.dataClasses.Worker;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class WorkerSelectedResponse extends MessageFromServerToClient {
    private final List<PossibleActions> possibleActions;
    private final Worker selectedWorker;

    @JsonCreator
    public WorkerSelectedResponse(@JsonProperty("type") Type type, @JsonProperty("username") String username,
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

}
