package it.polimi.ingsw.server.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.server.model.rules.RuleSetBase;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.Worker;

/**
 * Effects subclass
 * <p>
 * Generalizes all the gods effects that affect player's movement actions
 */
public class MovementStrategy extends RuleSetBase {

    /**
     * Determines if a given cell is reachable by a worker
     * <p>
     * By reachable we mean that the worker's cell and the target cell are adjacent and there is no dome on the
     * target cell
     *
     * @param worker the worker to be moved
     * @param cell   the target cell
     * @return true if the worker can reach the cell, false otherwise
     */
    protected boolean canGo(Worker worker, Cell cell) {
        return !cell.hasDome() && super.isCorrectDistance(worker, cell);
    }

    /**
     * Determines if a cell contains a worker which owner is the same as the strategy user
     *
     * @param cell the cell to check for a worker
     * @return true if the target cell contains a worker owned by another player, false otherwise
     */
    protected boolean isNotSameOwner(Cell cell) {
        return !game.getCurrentTurn().getCurrentPlayer().getWorkers().contains(cell.getOccupiedBy());
    }
}