package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;

public class PlayerMoveRequest extends Message {

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
