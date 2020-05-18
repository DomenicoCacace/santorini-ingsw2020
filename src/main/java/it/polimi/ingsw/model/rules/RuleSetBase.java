package it.polimi.ingsw.model.rules;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.ArrayList;
import java.util.List;

/**
 * The default rules
 * <p>
 * It defines the default rules for moving, building and setting up the turn:
 * <ul>
 *     <li><b>Setup</b>: using this ruleset, the player can place TWO workers at the beginning of the game
 *     (see {@link Worker} for details about the Workers placement)</li>
 *     <li><b>Turn Order</b>: the player MUST choose a worker, move the worker in another cell
 *     (see #isMoveActionValid()) and build (see #isBuildActionValid()) with THE SAME worker </li>
 * </ul>
 */
public class RuleSetBase implements RuleSetStrategy {

    @JsonIgnore
    protected Game game;
    protected int movesAvailable;
    protected int movesUpAvailable;
    protected int buildsAvailable;
    protected boolean hasMovedUp;
    protected Cell startingCell;
    protected Worker movedWorker;


    /**
     * Default constructor
     * @see #initialize()
     */
    public RuleSetBase() {
        initialize();
    }

    /**
     * Copy constructor
     * <p>
     * Used to restore the game status in case of unfortunate events on the server side
     * </p>
     *
     * @param ruleSetBase the ruleSet to restore
     * @param game the game code
     */
    public RuleSetBase(RuleSetStrategy ruleSetBase, Game game) {
        this.game = game;
        this.movesAvailable = ruleSetBase.getMovesAvailable();
        this.movesUpAvailable = ruleSetBase.getMovesUpAvailable();
        this.buildsAvailable = ruleSetBase.getBuildsAvailable();
        this.hasMovedUp = ruleSetBase.hasMovedUp();
        if (ruleSetBase.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(ruleSetBase.getMovedWorker().getPosition()).getOccupiedBy();
        else
            this.movedWorker = null;
    }

    /**
     * Sets the parameters for a new turn
     * <p>
     *     Using this ruleSet, a player is granted one movement and one building action, which have to be used in this
     *     order and by the same worker
     * </p>
     */
    protected void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 1;
        this.hasMovedUp = false;
        this.movedWorker = null;
    }

    /**
     * <i>movesAvailable</i> getter
     * @return the number of moves available
     */
    @Override
    public int getMovesAvailable() {
        return movesAvailable;
    }

    /**
     * <i>movesUpAvailable</i> getter
     * @return the number of moves available on a taller building
     */
    @Override
    public int getMovesUpAvailable() {
        return movesUpAvailable;
    }

    /**
     * <i>movesUpAvailable</i> setter
     * <p>
     *     Used when an effect has a malus on other players' available moves
     * </p>
     * @param num the number of moves up to be made available
     */
    @Override
    public void setMovesUpAvailable(int num) {
        this.movesUpAvailable = num;
    }

    /**
     * <i>hasMovedUp</i> getter
     * @return true if the player moved up during the last turn, false otherwise
     */
    @Override
    public boolean hasMovedUp() {
        return hasMovedUp;
    }

    /**
     * <i>buildsAvailable</i> getter
     * @return the number of buildings the player can build
     */
    @Override
    public int getBuildsAvailable() {
        return buildsAvailable;
    }

    /**
     * <i>movedWorker</i> getter
     * @return the worker which has been moved during the last turn (can be <i>null</i>)
     */
    @Override
    public Worker getMovedWorker() {
        return movedWorker;
    }

    /**
     * <i>game</i> setter
     * @param game the game in which the effect is used
     */
    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Provides the possible blocks buildable on a given cell
     * <p>
     *     Using this ruleSet, a worker will always be able to build only one kind of block, immediately higher
     *     than the one it is trying to build on
     * </p>
     * @param selectedCell the cell to get the buildable blocks for
     * @return a list of blocks that can be built on the given cell
     */
    @Override
    public List<Block> getBlocks(Cell selectedCell) {
        List<Block> buildingBlocks = new ArrayList<>();
        buildingBlocks.add(Block.values()[selectedCell.getBlock().getHeight() + 1]);
        return buildingBlocks;
    }

    /**
     * Applies end turn effects
     * <p>
     *     Using this ruleSet, the end turn effects simply resets the attributes changed during the turn
     * </p>
     */
    @Override
    public void doEffect() {
        initialize();
    }

    /**
     * Determines if the moveAction's target cell is a legal one
     * @param action the action to analyze
     * @return true if the cell is <i>walkable</i>, false otherwise
     */
    protected boolean isInsideWalkableCells(MoveAction action) {
        return getWalkableCells(action.getTargetWorker()).contains(action.getTargetCell());
    }

    /**
     * Provides a list of possible actions for a player to perform, based on the chosen worker
     * <p>
     *     Using this ruleSet, the possible actions for a worker are:
     *     <ul>
     *         <li>Change Worker/Move, if the worker  has not been moved yet</li>
     *         <li>Build, if the worker has been moved</li>
     *         <li>None, in any other case</li>
     *     </ul>
     * </p>
     * @param worker the worker to perform an action with
     * @return a list of possible performable actions
     */
    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = new ArrayList<>();
        if (getWalkableCells(worker).size() > 0) {
            possibleActions.add(PossibleActions.MOVE);
            possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);
        } else if (getBuildableCells(worker).size() > 0) {
            possibleActions.add(PossibleActions.BUILD);
        } else
            possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);
        return possibleActions;
    }

    /**
     * Determines if a moveAction is legal and applies it
     * <p>
     *     Using this ruleSet, a movement action is considered valid if the following conditions are all true:
     *     <ul>
     *         <li>no worker has been moved yet during the turn</li>
     *         <li>the target cell is a walkable cell (see {@linkplain #getWalkableCells(Worker)}) for the worker to be moved</li>
     *     </ul>
     * </p>
     * @param action the movement action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (isInsideWalkableCells(action)) {
            movesAvailable--;

            if (movesUpAvailable > 0)
                movesUpAvailable--;

            if (action.getTargetWorker().getPosition().heightDifference(action.getTargetCell()) == 1)
                hasMovedUp = true;
            movedWorker = action.getTargetWorker();
            startingCell = action.getStartingCell();
            return true;
        }
        return false;
    }

    /**
     * Determines if a buildAction is legal and applies it
     * <p>
     *     Using this ruleSet, a build action is considered valid if the following conditions are all true:
     *     <ul>
     *         <li>a worker has already been moved</li>
     *         <li>the worker to perform the action is the same which has been moved</li>
     *         <li>the cell to build on is a buildable cell (see {@linkplain #getBuildableCells(Worker)}) for the worker</li>
     *     </ul>
     * </p>
     * @param action the build action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (canBuild(action)) {
            buildsAvailable--;
            movesAvailable = 0;
            movesUpAvailable = 0;
            return true;
        }
        return false;
    }

    /**
     * Determines if the buildAction's target cell is a legal one
     * @param action the action to analyze
     * @return true if the cell is <i>buildable</i>, false otherwise
     * @see #isBuildActionValid(BuildAction)
     */
    protected boolean isInsideBuildableCells(BuildAction action) {
        return getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell());
    }

    /**
     * Determines if the worker chosen for the build action can actually build
     * @param action the action to analyze
     * @return true if the worker can build, false otherwise
     * @see #isBuildActionValid(BuildAction)
     */
    protected boolean canBuild(BuildAction action) {
        return isInsideBuildableCells(action) && isCorrectBlock(action) &&
                movedWorker == action.getTargetWorker();
    }

    /**
     * Determines if the block to be built can actually be built
     * @param action the action to analyze
     * @return true if the block can be placed, false otherwise
     * @see #isBuildActionValid(BuildAction)
     */
    protected boolean isCorrectBlock(BuildAction action) {
        return action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1);
    }

    /**
     * Determines if the win conditions are satisfied upon a movement action
     * <p>
     *     Using this ruleSet, a player can win only upon moving (following the rules already defined in
     *     {@linkplain #isMoveActionValid(MoveAction)}) on a level 3 building
     * </p>
     * @param action the action to analyze
     * @return true if the action led to victory, false otherwise
     */
    @Override
    public boolean checkWinCondition(MoveAction action) {
        return action.getTargetCell().getBlock().getHeight() == 3 &&
                startingCell.heightDifference(action.getTargetCell()) == 1;
    }

    /**
     * Determines if the lose conditions are satisfied upon a movement action
     * <p>
     *     Using this ruleSet, a player can lose upon a movement action if the worker which has been moved cannot build
     *     any block around it
     * </p>
     * @param moveAction the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    @Override
    public boolean checkLoseCondition(MoveAction moveAction) {
        return (getBuildableCells(moveAction.getTargetWorker()).size() == 0);
    }

    /**
     * Checks if the turn can begin
     * <p>
     *     Using this ruleSet, a player's turn can start if at least one of the player's workers can perform a movement
     *     action
     * </p>
     * @return true if there is at least one action to perform, false otherwise
     */
    @Override
    public boolean checkLoseCondition() {
        int legalCells = 0;
        for (Worker worker : game.getCurrentTurn().getCurrentPlayer().getWorkers())
            legalCells += getWalkableCells(worker).size();
        return legalCells == 0;
    }

    /**
     * Determines if the lose conditions are satisfied upon a build action
     * <p>
     *     Using this ruleSet, the player can never lose after it performs a build action; it might "trap itself", but
     *     in this case it will lose on the beginning of its next turn.
     * </p>
     * @param buildAction the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    @Override
    public boolean checkLoseCondition(BuildAction buildAction) {
        return false; //He can't lose after building
    }

    /**
     * Provides a list of cells on which the worker can walk on
     * <p>
     *     Using this ruleSet, a worker can walk on the cells adjacent to its starting cell which height difference is
     *     at most one compared to the starting cell (domes do not count), is not occupied by another worker and has no dome built on it
     * </p>
     * @param worker the worker to be moved
     * @return a list of <i>walkable</i> cells
     */
    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (movesAvailable > 0) {
            addWalkableCells(worker, cells);
        }
        return cells;
    }

    /**
     * Adds to a list the cells on which the given player can walk on
     * @param worker the worker to be moved
     * @param cells the list of walkable cells
     */
    protected void addWalkableCells(Worker worker, List<Cell> cells) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if ((!cell.hasDome() && cell.getOccupiedBy() == null) && isCorrectDistance(worker, cell))
                cells.add(cell);
        }
    }

    /**
     * Determines if a worker can reach a cell
     * <p>A cell is considered reachable if the height difference between the worker's cell and the target cell is at most 1,
     * and the movesUpAvailable counter has not been decreased</p>
     * @param worker the worker to be moved
     * @param cell the target cell
     * @return true if the cell is reachable, false otherwise
     */
    protected boolean isCorrectDistance(Worker worker, Cell cell) {
        return (worker.getPosition().heightDifference(cell) <= 0) || (worker.getPosition().heightDifference(cell) == 1 && movesUpAvailable > 0);
    }

    /**
     * Provides a list of cells on which the worker can build on
     * <p>
     *     Using this ruleSet, a worker can build on any cell adjacent to its starting cell
     * </p>
     * @param worker the worker to build with
     * @return a list of <i>buildable</i> cells
     */
    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (buildsAvailable > 0) {
            if (worker.equals(movedWorker)) {
                addBuildableCells(worker, cells);
            }
        }
        return cells;
    }

    /**
     * Adds to a list the cells on which the given player can build on
     * @param worker the worker to be moved
     * @param cells the list of buildable cells
     * @see #getBuildableCells(Worker)
     */
    protected void addBuildableCells(Worker worker, List<Cell> cells) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if (cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }
    }

    /**
     * Determines whether a player can end its turn
     * <p>
     *     Using this ruleSet, a player cannot end manually its turn
     * </p>
     * @return true if the player can end its turn, false otherwise
     */
    @Override
    public boolean canEndTurn() {
        return canEndTurnAutomatically();
    }

    /**
     * Determines whether a player can end its turn
     * <p>
     *     Using this ruleSet, a player turn is ended automatically right after it performs its build action
     *     <br>
     *         This method should never be invoked directly from the player
     * </p>
            * @return true if the player can end its turn, false otherwise
     */
    @Override
    public boolean canEndTurnAutomatically() {
        return (movesAvailable == 0 && buildsAvailable == 0);
    }

    /**
     * Creates a clone of this object
     * @param game the current game
     * @return a clone of this object
     */
    public RuleSetStrategy cloneStrategy(Game game) {
        return new RuleSetBase(this, game);
    }


}
