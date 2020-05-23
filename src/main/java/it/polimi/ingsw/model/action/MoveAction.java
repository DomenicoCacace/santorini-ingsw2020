package it.polimi.ingsw.model.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;

/**
 * Describes and manages a movement action
 */
public class MoveAction extends Action {

    /**
     * Default constructor
     *
     * @param targetWorker the worker to be moved
     * @param targetCell   the cell to move the worker to
     */
    public MoveAction(@JsonProperty("targetWorker") Worker targetWorker, @JsonProperty("targetCell") Cell targetCell) {
        super(targetWorker, targetCell);
    }

    /**
     * Forces an action to be applied
     */
    public void apply() {
        targetWorker.getPosition().setOccupiedBy(null);
        targetWorker.setPosition(targetCell);
        targetCell.setOccupiedBy(targetWorker);
    }

    /**
     * Checks if the action can be performed, based on the game status
     *
     * @param game the game object which validates the action
     * @throws IllegalActionException if the action cannot be performed
     */
    public void getValidation(Game game) throws IllegalActionException {
        game.validateMoveAction(this);
    }
}
