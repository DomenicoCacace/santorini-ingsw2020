package it.polimi.ingsw.server.model.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.server.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.shared.dataClasses.Block;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.Worker;

/**
 * Describes and manages a build action
 */
public class BuildAction extends Action {

    protected final Block targetBlock;

    /**
     * Default constructor
     *
     * @param targetWorker the worker performing the action
     * @param targetCell   the cell to build on
     * @param targetBlock  the block to build on the target cell
     */
    @JsonCreator
    public BuildAction(@JsonProperty("targetWorker") Worker targetWorker, @JsonProperty("targetCell") Cell targetCell, @JsonProperty("targetBlock") Block targetBlock) {
        super(targetWorker, targetCell);
        this.targetBlock = targetBlock;
    }

    /**
     * <i>targetBlock</i> getter
     *
     * @return the block to build
     */
    public Block getTargetBlock() {
        return targetBlock;
    }

    /**
     * Forces an action to be applied
     */
    public void apply() {
        targetCell.setBlock(targetBlock);
    }

    /**
     * Checks if the action can be performed, based on the game status
     *
     * @param game the game object which validates the action
     * @throws IllegalActionException if the action cannot be performed
     */
    public void getValidation(Game game) throws IllegalActionException {
        game.validateBuildAction(this);
    }
}
