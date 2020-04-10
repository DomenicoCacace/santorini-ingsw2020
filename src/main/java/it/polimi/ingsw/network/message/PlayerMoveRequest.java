package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

public class PlayerMoveRequest extends MessageRequest {


    public PlayerMoveRequest(String username, Cell targetCell, Worker targetWorker) {
        super(username, Content.PLAYER_MOVE);
    }
}
