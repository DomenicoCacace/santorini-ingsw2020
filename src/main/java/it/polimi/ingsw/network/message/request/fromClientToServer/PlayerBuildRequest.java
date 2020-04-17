package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.request.MessageRequest;

public class PlayerBuildRequest extends MessageRequest {

    public final Cell targetCell;
    public final Worker targetWorker;
    public final Block targetBlock;



    public PlayerBuildRequest(String username, Cell targetCell, Block targetBlock, Worker targetWorker) {
        super(username, Content.PLAYER_BUILD);
        this.targetCell = targetCell;
        this.targetBlock = targetBlock;
        this.targetWorker = targetWorker;

    }
}
