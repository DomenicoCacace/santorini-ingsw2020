package it.polimi.ingsw.model.action;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;

public class BuildAction extends Action {

    protected final Block targetBlock;

    public BuildAction(Worker targetWorker, Cell targetCell, Block targetBlock) {
        super(targetWorker, targetCell);
        this.targetBlock = targetBlock;
    }

    public Block getTargetBlock() {
        return targetBlock;
    }

    public void apply() {
        targetCell.setBlock(targetBlock);
    }

    public void getValidation(Game game) {
        game.validateBuildAction(this);
    }
}
