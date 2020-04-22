package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;

public class PlayerMoveRequest extends Message {

    private final Cell targetCell;
    private final Worker targetWorker;

    @JsonCreator
    public PlayerMoveRequest(@JsonProperty("username") String username, @JsonProperty("targetCell") Cell targetCell,
                             @JsonProperty("targetWorker") Worker targetWorker) {
        super(username, Content.PLAYER_MOVE);
        this.targetCell = targetCell;
        this.targetWorker = targetWorker;
    }

    public Cell getTargetCell() {
        return targetCell;
    }

    public Worker getTargetWorker() {
        return targetWorker;
    }
}
