package it.polimi.ingsw.model.action;

import it.polimi.ingsw.model.*;

import java.io.IOException;

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

    public void getValidation(Game game) throws IOException {
        game.validateBuildAction(this);
    }
}
