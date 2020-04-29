package it.polimi.ingsw.model.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;

public class BuildAction extends Action {

    protected final Block targetBlock;

    @JsonCreator
    public BuildAction(@JsonProperty("targetWorker") Worker targetWorker, @JsonProperty("targetCell") Cell targetCell, @JsonProperty("targetBlock") Block targetBlock) {
        super(targetWorker, targetCell);
        this.targetBlock = targetBlock;
    }

    public Block getTargetBlock() {
        return targetBlock;
    }

    public void apply() {
        targetCell.setBlock(targetBlock);
    }

    public void getValidation(Game game) throws IllegalActionException {
        game.validateBuildAction(this);
    }
}
