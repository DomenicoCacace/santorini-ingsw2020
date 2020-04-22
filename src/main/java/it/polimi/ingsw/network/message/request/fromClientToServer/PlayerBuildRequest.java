package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;

public class PlayerBuildRequest extends Message {

    private final Cell targetCell;
    private final Worker targetWorker;
    private final Block targetBlock;


    public PlayerBuildRequest(String username, Cell targetCell, Block targetBlock, Worker targetWorker) {
        super(username, Content.PLAYER_BUILD);
        this.targetCell = targetCell;
        this.targetBlock = targetBlock;
        this.targetWorker = targetWorker;
    }

    public Cell getTargetCell() {
        return targetCell;
    }

    public Worker getTargetWorker() {
        return targetWorker;
    }

    public Block getTargetBlock() {
        return targetBlock;
    }
}
