package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

public class PlayerBuildRequest extends MessageRequest {

    public final Type type;

    public PlayerBuildRequest(String username, Cell targetCell, Block targetBlock, Worker targetWorker) {
        super(username, Content.PLAYER_BUILD);
        type = Type.REQUEST;
    }
}
