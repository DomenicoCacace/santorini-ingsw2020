package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Swap position with opponents' workers workers when moving
 * <p>
 * This effect alters the workers' walkable cells: workers can swap their position with an opponent worker if it is
 * in an adjacent cell
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Swap extends MovementStrategy {

    private final List<Worker> stuckWorkers;

    /**
     * Default constructor
     *
     * @see #initialize()
     */
    public Swap() {
        super();
        stuckWorkers = new ArrayList<>();
    }

    /**
     * Copy constructor
     *
     * @param swap the strategy to clone
     * @param game the game in which the effect is used
     */
    private Swap(Swap swap, Game game) {
        this.game = game;
        this.movesAvailable = swap.getMovesAvailable();
        this.movesUpAvailable = swap.getMovesUpAvailable();
        this.buildsAvailable = swap.getBuildsAvailable();
        this.hasMovedUp = swap.hasMovedUp();
        if (swap.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(swap.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        this.stuckWorkers = swap.stuckWorkers;
    }

    /**
     * Applies the action and eventually swaps the target cell's occupant position
     *
     * @param action the action to be performed
     */
    public void swapAction(MoveAction action) {
        if (action.getTargetCell().getOccupiedBy() != null) {

            Cell myPreviousCell = action.getStartingCell();
            Cell myAfterCell = action.getTargetCell();
            Worker myWorker = action.getTargetWorker();
            Worker opponentWorker = action.getTargetCell().getOccupiedBy();

            if (myWorker.getPosition().heightDifference(myAfterCell) == 1)
                hasMovedUp = true;

            myWorker.setPosition(myAfterCell);
            myAfterCell.setOccupiedBy(myWorker);
            opponentWorker.setPosition(myPreviousCell);
            myPreviousCell.setOccupiedBy(opponentWorker);
        }
    }

    /**
     * Determines if a moveAction is legal and applies it
     * <p>
     * g this ruleSet, a movement action is considered valid if the following conditions are all true:
     * <ul>
     *     <li>no worker has been moved yet during the turn</li>
     *     <li>the target cell is a walkable cell (see {@linkplain #getWalkableCells(Worker)}) for the worker to be moved</li>
     * </ul>
     *
     * @param action the movement action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (movesAvailable > 0 && isInsideWalkableCells(action)) {
            movedWorker = action.getTargetWorker();
            swapAction(action);
            movesAvailable--;
            startingCell = action.getStartingCell();
            if (movesUpAvailable > 0)
                movesUpAvailable--;
            return true;
        }
        return false;
    }

    /**
     * Provides a list of cells on which the worker can walk on
     * <p>
     * Using this ruleSet, a worker can walk on the cells adjacent to its starting cell which height difference is
     * at most one compared to the starting cell (domes do not count) and has no dome built on it; a worker can walk
     * on a cell occupied by another worker (if the opponent worker's cell satisfies the condition above mentioned).
     * If moving in a cell determines an immediate loss, the cell causing this behavior is not considered walkable;
     * if all the cells lead to an immediate loss, the rule above is no longer applied and the player is free to choose
     * which move to make before losing (see {@linkplain #canBuildOnAtLeastOneCell(Cell)})
     *
     * @param worker the worker to be moved
     * @return a list of <i>walkable</i> cells
     */
    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        List<Cell> losingCells = new ArrayList<>();
        if (movesAvailable > 0) {
            for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
                if (canGo(worker, cell) && (cell.getOccupiedBy() == null || isNotSameOwner(cell))) {
                    if (!canBuildOnAtLeastOneCell(cell))
                        losingCells.add(cell);
                    cells.add(cell);
                }
            }
        }
        if (losingCells.size() == cells.size()) {
            if (!stuckWorkers.contains(worker))
                stuckWorkers.add(worker);
            if (stuckWorkers.size() < 2)
                cells.removeAll(losingCells);
        } else
            cells.removeAll(losingCells);
        return cells;
    }

    /**
     * Checks if, after moving in a given cell, the worker can perform a build action
     * <p>
     * This method is needed to avoid the player to make a move that would lead it to an immediate loss
     *
     * @param targetCell
     * @return
     */
    private boolean canBuildOnAtLeastOneCell(Cell targetCell) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(targetCell)) {
            if (!cell.hasDome() && cell.getOccupiedBy() == null)
                return true;
        }
        return false;
    }

    /**
     * Creates a clone of this object
     *
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new Swap(this, game);
    }

}