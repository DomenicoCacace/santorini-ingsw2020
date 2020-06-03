package it.polimi.ingsw.network.message.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class EndTurnEvent extends MessageFromServerToClient {
    private final String newCurrentPlayerName;
    private final List<Cell> workersCells;

    @JsonCreator
    public EndTurnEvent(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("newCurrentPlayerName") String newCurrentPlayerName,
                        @JsonProperty("workersCells") List<Cell> workersCells) {
        super(username, type);
        this.newCurrentPlayerName = newCurrentPlayerName;
        this.workersCells = workersCells;
    }

    public List<Cell> getWorkersCells() {
        return workersCells;
    }

    public String getNewCurrentPlayerName() {
        return newCurrentPlayerName;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onTurnEnd(this);
    }

}
