package it.polimi.ingsw.model.action;

import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;

/**
 * Abstraction for Workers' actions
 * <p>
 *     Each action
*/
public abstract class Action {

    protected final Cell targetCell;
    protected final Worker targetWorker;

    /**
     * Default constructor
     * @param targetWorker the worker performing the action
     * @param targetCell the cell to perform the action on
     */
    protected Action(Worker targetWorker, Cell targetCell) {
        this.targetCell = targetCell;
        this.targetWorker = targetWorker;
    }

    /**
     * <i>targetWorker</i> getter
     * @return the worker performing the action
     */
    public Worker getTargetWorker() {
        return targetWorker;
    }

    /**
     * <i>startingCell</i> getter
     * @return the worker's current position
     */
    public Cell getStartingCell() {
        return this.targetWorker.getPosition();
    }

    /**
     * <i>targetCell</i> getter
     * @return the action's target cell
     */
    public Cell getTargetCell() {
        return targetCell;
    }

    /**
     * Checks if the action can be performed, based on the game status
     * @param game the game object which validates the action
     * @throws IllegalActionException if the action cannot be performed
     */
    public abstract void getValidation(Game game) throws IllegalActionException;

    /**
     * Forces an action to be applied
     */
    public abstract void apply();

}
