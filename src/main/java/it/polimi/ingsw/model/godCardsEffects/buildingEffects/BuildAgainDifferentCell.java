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
 *     choose if it wants to build again (not on top of the previously built cell) or pass the turn
*/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BuildAgainDifferentCell extends BuildingStrategy {

    private Cell chosenCell;

    /**
     * Copy constructor
     * @param buildAgainDifferentCell the strategy to clone
     * @param game the game in which the effect is used
     */
    private BuildAgainDifferentCell(BuildAgainDifferentCell buildAgainDifferentCell, Game game) {
        this.game = game;
        this.movesAvailable = buildAgainDifferentCell.getMovesAvailable();
        this.movesUpAvailable = buildAgainDifferentCell.getMovesUpAvailable();
        this.buildsAvailable = buildAgainDifferentCell.getBuildsAvailable();
        this.hasMovedUp = buildAgainDifferentCell.hasMovedUp();
        if (buildAgainDifferentCell.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(buildAgainDifferentCell.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        chosenCell = game.getGameBoard().getCell(buildAgainDifferentCell.chosenCell);
    }

    /**
     * Default constructor
     * @see #initialize()
     */
    public BuildAgainDifferentCell() {
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
     *     Note that in the rare case that all the possible actions lead the player to a loss, it must
     *     perform a move anyway, since the game board has to be altered before removing its workers from the board
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
     *         <li>the worker to perform the action has already built once</li>
     *     </ul>
    * @param action the build action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (canBuild(action)) {
            buildsAvailable--;
            chosenCell = action.getTargetCell();
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
            else {
                for (Cell cell : super.getBuildableCells(worker))
                    if (cell != chosenCell)
                        secondBuild.add(cell);
            }
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
        return new BuildAgainDifferentCell(this, game);
    }
}
