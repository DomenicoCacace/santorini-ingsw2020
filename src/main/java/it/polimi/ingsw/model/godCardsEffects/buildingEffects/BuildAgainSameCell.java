package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Move, build, build again (optional)
 * <p>
 *     This effect alters the player's build action; the player can play the turn as usual, but after it builds, it can
 *     choose if it wants to build again on top of the previously built cell (no dome can be built on the second build
 *     action) or pass the turn
*/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BuildAgainSameCell extends BuildingStrategy {

    private Cell chosenCell;

    /**
     * Copy constructor
     * @param buildAgainSameCell the strategy to clone
     * @param game the game in which the effect is used
     */
    private BuildAgainSameCell(BuildAgainSameCell buildAgainSameCell, Game game) {
        this.game = game;
        this.movesAvailable = buildAgainSameCell.getMovesAvailable();
        this.movesUpAvailable = buildAgainSameCell.getMovesUpAvailable();
        this.buildsAvailable = buildAgainSameCell.getBuildsAvailable();
        this.hasMovedUp = buildAgainSameCell.hasMovedUp();
        if (buildAgainSameCell.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(buildAgainSameCell.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        chosenCell = game.getGameBoard().getCell(buildAgainSameCell.chosenCell);

    }

    /**
     * Default constructor
     * @see #initialize()
     */
    public BuildAgainSameCell() {
        super();
    }

    /**
     * Sets the parameters for a new turn
     * <p>
     *     Using this ruleSet, a player is granted one movement and two building action, to be performed by the same worker
     *     following the rules mentioned in the class documentation.
    */
    @Override
    public void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp = false;
        this.movedWorker = null;
        this.chosenCell = null;
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
     *         <li>Build, if the worker has been moved</li>
     *         <li>Build, Pass turn, if the worker has built once</li>
     *         <li>None, in any other case</li>
     *     </ul>
    * @param worker the worker to perform an action with
     * @return a list of possible performable actions
     */
    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = super.getPossibleActions(worker);
        if (buildsAvailable == 1)
            possibleActions.add(PossibleActions.PASS_TURN);
        return possibleActions;
    }

    /**
     * Determines if a buildAction is legal and applies it
     * <p>
     *     Using this ruleSet, a build action is considered valid if the following conditions are all true:
     *     <ul>
     *         <li>the worker to perform the action has already been moved</li>
     *         <li>the worker to perform the action has already built once, the target cell is the same the worker
     *         already built on and the block to be built is not a dome</li>
     *     </ul>
    * @param action the build action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (this.buildsAvailable > 0 && isInsideBuildableCells(action) &&
                isCorrectBlock(action) && movedWorker == action.getTargetWorker()) {
            buildsAvailable--;
            chosenCell = action.getTargetCell();
            if (action.getTargetBlock().getHeight() == 3 || action.getTargetBlock().getHeight() == 4)
                buildsAvailable = 0;
            return true;
        }
        return false;
    }

    /**
     * Provides a list of cells on which the worker can build on
     * <p>
     *     Using this ruleSet, a worker can build on any cell adjacent to its cell for the first action, and on any cell
     *     adjacent to its cell minus the cell it already built on if it already built once during the turn.
    * @param worker the worker to build with
     * @return a list of <i>buildable</i> cells
     */
    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> secondBuild = new ArrayList<>();
        if (this.buildsAvailable > 0) {
            if (chosenCell == null)
                secondBuild = super.getBuildableCells(worker);
            else
                secondBuild.add(chosenCell);
        }
        return secondBuild;
    }

    /**
     * Determines whether a player can end its turn
     * <p>
     *     Using this ruleSet, a player can end its turn after it performs the first build action correctly
    * @return true if the player can end its turn, false otherwise
     */
    @Override
    public boolean canEndTurn() {
        return (movesAvailable == 0 && buildsAvailable <= 1);
    }

    /**
     * Creates a clone of this object
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new BuildAgainSameCell(this, game);
    }


}
