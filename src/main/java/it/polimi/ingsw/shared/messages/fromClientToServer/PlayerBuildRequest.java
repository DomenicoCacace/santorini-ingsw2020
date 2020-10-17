package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Block;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.Worker;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

public class PlayerBuildRequest extends MessageFromClientToServer {

    private final Cell targetCell;
    private final Worker targetWorker;
    private final Block targetBlock;

    @JsonCreator
    public PlayerBuildRequest(@JsonProperty("username") String username, @JsonProperty("targetCell") Cell targetCell,
                              @JsonProperty("targetBlock") Block targetBlock, @JsonProperty("targetWorker") Worker targetWorker) {

        super(username, Type.CLIENT_REQUEST);
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

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.managePlayerBuild(this);
    }
}
