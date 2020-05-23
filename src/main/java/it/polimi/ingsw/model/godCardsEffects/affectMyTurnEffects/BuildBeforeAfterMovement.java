package it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Build (optional), Move, Build
 * * <br>
 * This effect alters the player's turn order; either, the player can:
 * <ul>
 *     <li>Play the turn as usual, following the {@link it.polimi.ingsw.model.rules.RuleSetBase}</li>
 *     <li>Build a block, move on a level not higher than the current one, then build again</li>
 * </ul>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BuildBeforeAfterMovement extends AffectMyTurnStrategy {

    private boolean hasBuiltBefore;
    private Worker builder;
    private int stuckWorkers;

    /**
     * Copy constructor
     *
     * @param buildBeforeAfterMovement the strategy to clone
     * @param game                     the game in which the effect is used
     */
    private BuildBeforeAfterMovement(BuildBeforeAfterMovement buildBeforeAfterMovement, Game game) {
        this.game = game;
        this.movesAvailable = buildBeforeAfterMovement.getMovesAvailable();
        this.movesUpAvailable = buildBeforeAfterMovement.getMovesUpAvailable();
        this.buildsAvailable = buildBeforeAfterMovement.getBuildsAvailable();
        this.hasMovedUp = buildBeforeAfterMovement.hasMovedUp();
        if (buildBeforeAfterMovement.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(buildBeforeAfterMovement.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        this.hasBuiltBefore = buildBeforeAfterMovement.hasBuiltBefore;
        this.builder = game.getGameBoard().getCell(buildBeforeAfterMovement.builder.getPosition()).getOccupiedBy();
        this.stuckWorkers = buildBeforeAfterMovement.stuckWorkers;
    }

    /**
     * Default constructor
     *
     * @see #initialize()
     */
    public BuildBeforeAfterMovement() {
        super();
    }

    /**
     * Sets the parameters for a new turn
     * <p>
     * Using this ruleSet, a player is granted one movement and two building action, to be performed by the same worker
     * following the rules mentioned in the class documentation.
     * <br>
     * The attribute {@link #stuckWorkers} is needed because, using this effect, it is possible to lose after
     * performing a build action; this counter keeps track of the number of the controlled workers that can perform
     * no action during the turn
     */
    @Override
    public void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp = false;
        this.hasBuiltBefore = false;
        this.movedWorker = null;
        this.builder = null;
        this.stuckWorkers = 0;
    }

    /**
     * Applies end turn effects
     * <p>
     * Using this ruleSet, the end turn effects simply resets the attributes changed during the turn
     */
    @Override
    public void doEffect() {
        initialize();
    }

    /**
     * Provides a list of possible actions for a player to perform, based on the chosen worker
     * <p>
     * g this ruleSet, the possible actions for a worker are:
     * <ul>
     *     <li>Change Worker/Move/Build, if the worker  has not been moved yet</li>
     *     <li>Move, if the worker built as its first action</li>
     *     <li>Build, if the worker has been moved</li>
     *     <li>None, in any other case</li>
     * </ul>
     * Note that in the rare case that all the possible actions lead the player to a loss, it must
     * perform a move anyway, since the game board has to be altered before removing its workers from the board
     *
     * @param worker the worker to perform an action with
     * @return a list of possible performable actions
     */
    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = new ArrayList<>();
        if (buildsAvailable > 0) {
            if (movesAvailable == 0 && !hasBuiltBefore) {
                possibleActions = super.getPossibleActions(worker);
            } else if (movesAvailable == 0 && worker == builder) { //this is useful for the view: highlighting the correct cells
                possibleActions = super.getPossibleActions(worker);
            } else if (movesAvailable == 1 && !hasBuiltBefore) {
                if (getWalkableCells(worker).size() == 0) {
                    if (stuckWorkers == 2) {
                        List<Cell> buildableCells = new ArrayList<>();
                        addBuildableCells(worker, buildableCells);
                        if (buildableCells.size() > 0)
                            possibleActions.add(PossibleActions.BUILD);
                    }
                } else if (buildableCellsBeforeMoving(worker).size() > 0)
                    possibleActions.add(PossibleActions.BUILD);
                if (getWalkableCells(worker).size() > 0)
                    possibleActions.add(PossibleActions.MOVE);
                possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);


            } else if (movesAvailable == 1 && worker == builder)
                possibleActions.add(PossibleActions.MOVE);
        }

        return possibleActions;
    }

    /**
     * Determines if a moveAction is legal and applies it
     * <p>
     * g this ruleSet, a movement action is considered valid if the following conditions are all true:
     * <ul>
     *     <li>no worker has been moved yet during the turn OR the worker has built but has not been moved</li>
     *     <li>the target cell is a walkable cell (see {@linkplain #getWalkableCells(Worker)}) for the worker to be moved</li>
     * </ul>
     *
     * @param action the movement action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (!hasBuiltBefore && super.isMoveActionValid(action)) {
            buildsAvailable--;
            startingCell = action.getStartingCell();
            return true;
        }
        return super.isMoveActionValid(action);
    }

    /**
     * Determines if a buildAction is legal and applies it
     * <p>
     * g this ruleSet, a build action is considered valid if the following conditions are all true:
     * <ul>
     *     <li>no worker has been moved yet OR the worker to perform the action has already been moved</li>
     *     <li>the cell to build on is a buildable cell (see {@linkplain #getBuildableCells(Worker)}) for the worker</li>
     * </ul>
     *
     * @param action the build action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (this.buildsAvailable > 0 && isInsideBuildableCells(action) && isCorrectBlock(action)) {
            if (movedWorker == null) {
                hasBuiltBefore = true;
                builder = action.getTargetWorker();
                buildsAvailable--;
            } else if (movedWorker == action.getTargetWorker())
                buildsAvailable = 0;
            return true;
        }
        return false;
    }

    /**
     * Checks if the turn can begin
     * <p>
     * Using this ruleSet, a player's turn can start if at least one of the player's workers can perform a movement
     * action
     *
     * @return true if there is at least one action to perform, false otherwise
     */
    @Override
    public boolean checkLoseCondition() {
        List<Cell> legalCells = new ArrayList<>();
        for (Worker worker : game.getCurrentTurn().getCurrentPlayer().getWorkers()) {
            legalCells = getWalkableCells(worker);
            if (legalCells.size() == 0) {
                stuckWorkers++;
                legalCells = getBuildableCells(worker);
            }
        }
        return legalCells.size() == 0;
    }

    /**
     * Determines if the lose conditions are satisfied upon a build action
     * <p>
     * Using this ruleSet, the player can lose after performing a build action: since the worker cannot be moved
     * upwards if the player decides to build first, it can lose if, after placing the building block, all the
     * cells around him become inaccessible
     *
     * @param buildAction the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    @Override
    public boolean checkLoseCondition(BuildAction buildAction) {
        return stuckWorkers == 2;
    }

    /**
     * Provides a list of cells on which the worker can walk on
     * <p>
     * Using this ruleSet, a worker can walk on the cells adjacent to its starting cell which height difference is
     * at most one compared to the starting cell (domes do not count), is not occupied by another worker and has no dome built on it
     *
     * @param worker the worker to be moved
     * @return a list of <i>walkable</i> cells
     */
    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> canGoCells = new ArrayList<>();
        if (hasBuiltBefore) {
            if (worker == builder) {
                for (Cell cell : super.getWalkableCells(worker)) {
                    if (worker.getPosition().heightDifference(cell) <= 0)
                        canGoCells.add(cell);
                }
            }
            return canGoCells;
        }
        return super.getWalkableCells(worker);
    }

    /**
     * Provides a list of cells on which the worker can build on
     * <p>
     * Using this ruleSet, a worker can build on any cell adjacent to its cell, in both the cases mentioned above
     *
     * @param worker the worker to build with
     * @return a list of <i>buildable</i> cells
     */
    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (stuckWorkers < 2) {
            if (buildsAvailable > 0) {
                if (movesAvailable == 0 && !hasBuiltBefore) {
                    cells = super.getBuildableCells(worker);
                } else if (movesAvailable == 0 && worker == builder) { //this is useful for the view: highlighting the correct cells
                    cells = super.getBuildableCells(worker);
                } else if (movesAvailable == 1 && !hasBuiltBefore) {
                    cells = buildableCellsBeforeMoving(worker);
                }
            }
        } else addBuildableCells(worker, cells);
        return cells;
    }

    /**
     * Provides the list of cells the given worker can build on before the player performs a movement action
     *
     * @param worker the worker to build with
     * @return a list of <i>buildable</i> cells
     */
    private List<Cell> buildableCellsBeforeMoving(Worker worker) {
        List<Cell> buildableCells = new ArrayList<>();

        int cellsOnMyLevel = 0;
        int heightDifference;
        Cell cellOnMyLevel = null;
        super.addBuildableCells(worker, buildableCells);
        for (Cell cell : buildableCells) {
            heightDifference = worker.getPosition().heightDifference(cell);
            if (heightDifference < 0) {
                return buildableCells;
            } else if (heightDifference == 0) {
                cellsOnMyLevel++;
                if (cellsOnMyLevel > 1)
                    return buildableCells;
                else
                    cellOnMyLevel = cell;
            }
        }
        if (cellsOnMyLevel == 1) {
            buildableCells.remove(cellOnMyLevel);
        } else
            buildableCells = new ArrayList<>();
        return buildableCells;
    }

    /**
     * Creates a clone of this object
     *
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new BuildBeforeAfterMovement(this, game);
    }


}
