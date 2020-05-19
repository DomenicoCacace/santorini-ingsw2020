package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Move, move(optional), build
 * <p>
 *     This effect alters the player's movement actions; the player must perform a movement action, then it can choose
 *     to perform another movement action (it cannot move on the same cell its worker started the turn on); after that,
 *     it must perform a build action to end its turn
*/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MoveAgain extends MovementStrategy {

    private Cell startingCell;

    /**
     * Default constructor
     * @see #initialize()
     */
    public MoveAgain() {
        super();
    }

    /**
     * Copy constructor
     * @param moveAgain the strategy to clone
     * @param game the game in which the effect is used
     */
    private MoveAgain(MoveAgain moveAgain, Game game) {
        this.game = game;
        this.movesAvailable = moveAgain.getMovesAvailable();
        this.movesUpAvailable = moveAgain.getMovesUpAvailable();
        this.buildsAvailable = moveAgain.getBuildsAvailable();
        this.hasMovedUp = moveAgain.hasMovedUp();
        if (moveAgain.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(moveAgain.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        this.startingCell = game.getGameBoard().getCell(moveAgain.startingCell);
    }

    /**
     * Sets the parameters for a new turn
     * <p>
     *     Using this ruleSet, a player is granted two movement and one building action, to be performed by the same worker
     *     following the rules mentioned in the class documentation.
    */
    @Override
    public void initialize() {
        this.movesAvailable = 2;
        this.movesUpAvailable = 2;
        this.buildsAvailable = 1;
        this.hasMovedUp = false;
        this.movedWorker = null;
        this.startingCell = null;
    }

    /**
     * Applies end turn effects
     * <p>
     *     Using this ruleSet, the end turn effects simply resets the attributes changed during the turn
    */
    @Override
    public void doEffect() {
        initialize();
    }

    /**
     * Provides a list of possible actions for a player to perform, based on the chosen worker
     * <p>
     *     Using this ruleSet, the possible actions for a worker are:
     *     <ul>
     *         <li>Change Worker/Move, if the worker  has not been moved yet</li>
     *         <li>Move, Build, if the worker has been moved once</li>
     *         <li>Build, if the worker has been moved once</li>
     *         <li>None, in any other case</li>
     *     </ul>
     * @param worker the worker to perform an action with
     * @return a list of possible performable actions
     */
    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = new ArrayList<>();
        if (movesAvailable == 1) {
            possibleActions.add(PossibleActions.BUILD);
            if (getWalkableCells(worker).size() > 0)
                possibleActions.add(PossibleActions.MOVE);
        } else possibleActions = super.getPossibleActions(worker);
        return possibleActions;
    }

    /**
     * Determines if a moveAction is legal and applies it
     * <p>
     *     Using this ruleSet, a movement action is considered valid if the following conditions are all true:
     *     <ul>
     *         <li>no worker has been moved yet during the turn OR the worker to be moved has already been moved once during the turn</li>
     *         <li>the target cell is a walkable cell (see {@linkplain #getWalkableCells(Worker)}) for the worker to be moved</li>
     *         <li>in case of the second movement, the target cell must not be the same as the cell from which the worker
     *         started its turn</li>
     *     </ul>
     * @param action the movement action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isMoveActionValid(MoveAction action) {
        int x, y;
        if (movedWorker == null && super.isMoveActionValid(action)) {
            x = action.getTargetWorker().getPosition().getCoordX();
            y = action.getTargetWorker().getPosition().getCoordY();
            startingCell = game.getGameBoard().getCell(x, y);
            return true;
        } else if (movedWorker == action.getTargetWorker())
            return super.isMoveActionValid(action);
        return false;
    }

    /**
     * Provides a list of cells on which the worker can walk on
     * <p>
     *     Using this ruleSet, a worker can walk on the cells adjacent to its starting cell which height difference is
     *     at most one compared to the starting cell; in case the player decides to move a second time, the worker can move
     *     into a cell adjacent to its current position, except for the cell it started its turnn from
    * @param worker the worker to be moved
     * @return a list of <i>walkable</i> cells
     */
    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> adjacentCells = super.getWalkableCells(worker);
        if (movedWorker != null)
            adjacentCells.remove(startingCell);
        return adjacentCells;
    }

    /**
     * Determines if the lose conditions are satisfied upon a movement action
     * <p>
     *     Using this ruleSet, a player can lose upon a movement action if the worker which has been moved cannot build
     *     any block around it and it already used all of its movement actions
    * @param moveAction the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    @Override
    public boolean checkLoseCondition(MoveAction moveAction) {
        return (getBuildableCells(moveAction.getTargetWorker()).size() == 0 && movesAvailable == 0);
    }

    /**
     * Creates a clone of this object
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new MoveAgain(this, game);
    }
}
