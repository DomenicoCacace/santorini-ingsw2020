package it.polimi.ingsw.network.message.request;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

public class PlayerMoveRequest extends MessageRequest {

    public final Cell targetCell;
    public final Worker targetWorker;


    public PlayerMoveRequest(String username, Cell targetCell, Worker targetWorker) {
        super(username, Content.PLAYER_MOVE);
        this.targetCell= targetCell;
        this.targetWorker = targetWorker;
    }

    public Cell getTargetCell() {
        return targetCell;
    }
}
