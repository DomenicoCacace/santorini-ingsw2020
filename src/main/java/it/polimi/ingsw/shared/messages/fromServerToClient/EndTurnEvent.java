package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

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
