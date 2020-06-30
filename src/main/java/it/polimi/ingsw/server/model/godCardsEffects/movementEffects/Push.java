package it.polimi.ingsw.server.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.shared.dataClasses.GameBoard;
import it.polimi.ingsw.server.model.action.Action;
import it.polimi.ingsw.server.model.action.MoveAction;
import it.polimi.ingsw.server.model.rules.RuleSetStrategy;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Kick opponent workers when moving
 * <p>
 * This effect alters the workers' walkable cells: workers can push opponent an opponent worker in the cell behind
 * them (see {@linkplain GameBoard#getCellBehind(Cell, Cell)})
 */
public class Push extends MovementStrategy {

    /**
     * Default constructor
     *
     * @see #initialize()
     */
    public Push() {
        super();
    }

    /**
     * Copy constructor
     *
     * @param push the strategy to clone
     * @param game the game in which the effect is used
     */
    private Push(Push push, Game game) {
        this.game = game;
        this.movesAvailable = push.getMovesAvailable();
        this.movesUpAvailable = push.getMovesUpAvailable();
        this.buildsAvailable = push.getBuildsAvailable();
        this.hasMovedUp = push.hasMovedUp();
        if (push.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(push.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    /**
     * Determines if an opponent worker can be pushed
     * <p>
     * This method checks if the <i>cell behind</i> the opponent worker exists and is not occupied by another worker
     * nor a dome
     *
     * @param myCell     the worker's starting position
     * @param targetCell the cell containing the worker to push
     * @return true if the the opponent's worker can be pushed
     */
    boolean canPush(Cell myCell, Cell targetCell) {
        return game.getGameBoard().getCellBehind(myCell, targetCell) != null &&
                game.getGameBoard().getCellBehind(myCell, targetCell).getOccupiedBy() == null &&
                !game.getGameBoard().getCellBehind(myCell, targetCell).hasDome();
    }

    /**
     * Forces the opponent worker to be moved on the cell behind it
     *
     * @param action the action performed by the current player's worker
     */
    void opponentAction(MoveAction action) {
        if (action.getTargetCell().getOccupiedBy() != null) {
            Cell pushCell = game.getGameBoard().getCellBehind(action.getStartingCell(), action.getTargetCell()); //Assign to pushCell the Cell that's "behind" the opponent
            Action opponentMoveAction = new MoveAction(action.getTargetCell().getOccupiedBy(), pushCell);
            opponentMoveAction.apply();
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
        if (super.isMoveActionValid(action)) {
            startingCell = action.getStartingCell();
            opponentAction(action);
            return true;
        }
        return false;
    }

    /**
     * Provides a list of cells on which the worker can walk on
     * <p>
     * Using this ruleSet, a worker can walk on the cells adjacent to its starting cell which height difference is
     * at most one compared to the starting cell (domes do not count) and has no dome built on it; a worker can walk
     * on a cell occupied by another worker if it can be pushed (see {@linkplain #canPush(Cell, Cell)})
     *
     * @param worker the worker to be moved
     * @return a list of <i>walkable</i> cells
     */
    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (movesAvailable > 0) {
            for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {

                if (canGo(worker, cell) &&
                        ((cell.getOccupiedBy() == null) ||
                                (cell.getOccupiedBy() != null && canPush(worker.getPosition(), cell) && isNotSameOwner(cell))))
                        cells.add(cell);
                    }
                }
        return cells;
    }

    /**
     * Creates a clone of this object
     *
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new Push(this, game);
    }
}
