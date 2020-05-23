package it.polimi.ingsw.model.rules;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects.BuildBeforeAfterMovement;
import it.polimi.ingsw.model.godCardsEffects.affectOpponentTurnEffects.CannotMoveUp;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainDifferentCell;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainSameCell;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.MoveAgain;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Swap;
import it.polimi.ingsw.model.godCardsEffects.winConditionEffects.Down2Levels;

import java.util.List;

/**
 * Strategy interface for the implementation of the <a href=https://en.wikipedia.org/wiki/Strategy_pattern>Strategy pattern</a>
 * on the various gods effects
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Effect Name")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "stratId", scope = RuleSetStrategy.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleSetBase.class, name = "RuleSetBase"),
        @JsonSubTypes.Type(value = Push.class, name = "Push"),
        @JsonSubTypes.Type(value = Swap.class, name = "Swap"),
        @JsonSubTypes.Type(value = MoveAgain.class, name = "Move Again"),
        @JsonSubTypes.Type(value = BuildAgainDifferentCell.class, name = "Build again in different cell"),
        @JsonSubTypes.Type(value = BuildAgainSameCell.class, name = "Build again in same cell"),
        @JsonSubTypes.Type(value = BuildDome.class, name = "Build Dome"),
        @JsonSubTypes.Type(value = CannotMoveUp.class, name = "Cannot move up"),
        @JsonSubTypes.Type(value = BuildBeforeAfterMovement.class, name = "BuildBeforeAfterMovement"),
        @JsonSubTypes.Type(value = Down2Levels.class, name = "Down 2 Levels")
})
public interface RuleSetStrategy {

    /**
     * Applies end turn effects
     */
    void doEffect();

    /**
     * <i>movesUpAvailable</i> getter
     *
     * @return the number of moves available on a taller building
     */
    int getMovesUpAvailable(); //Testing purpose only

    /**
     * <i>movesUpAvailable</i> setter
     * <p>
     * Used when an effect has a malus on other players' available moves
     *
     * @param num the number of moves up to be made available
     */
    void setMovesUpAvailable(int num);

    /**
     * <i>movesAvailable</i> getter
     *
     * @return the number of moves available
     */
    int getMovesAvailable(); //Testing purpose only

    /**
     * <i>hasMovedUp</i> getter
     *
     * @return true if the player moved up during the last turn, false otherwise
     */
    boolean hasMovedUp(); //Testing purpose only

    /**
     * <i>buildsAvailable</i> getter
     *
     * @return the number of buildings the player can build
     */
    int getBuildsAvailable(); //Testing purpose only

    /**
     * <i>movedWorker</i> getter
     *
     * @return the worker which has been moved during the last turn (can be <i>null</i>)
     */
    Worker getMovedWorker(); //Testing purpose only

    /**
     * Provides a list of possible actions for a player to perform, based on the chosen worker
     *
     * @param worker the worker to perform an action with
     * @return a list of possible performable actions
     */
    List<PossibleActions> getPossibleActions(Worker worker);

    /**
     * Determines if a moveAction is legal and applies it
     *
     * @param action the movement action to validate
     * @return true if the action has been applied, false otherwise
     */
    boolean isMoveActionValid(MoveAction action);

    /**
     * Determines if a buildAction is legal and applies it
     *
     * @param action the build action to validate
     * @return true if the action has been applied, false otherwise
     */
    boolean isBuildActionValid(BuildAction action);

    /**
     * Determines if the win conditions are satisfied upon a movement action
     *
     * @param action the action to analyze
     * @return true if the action led to victory, false otherwise
     */
    boolean checkWinCondition(MoveAction action);

    /**
     * Determines if the lose conditions are satisfied upon a movement action
     *
     * @param moveAction the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    boolean checkLoseCondition(MoveAction moveAction);

    /**
     * Checks if the turn can begin, checking for both players to be <i>free</i>
     *
     * @return true if there is at least one action to perform, false otherwise
     */
    boolean checkLoseCondition();

    /**
     * Determines if the lose conditions are satisfied upon a movement action
     *
     * @param buildAction the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    boolean checkLoseCondition(BuildAction buildAction);

    /**
     * Provides a list of cells on which the worker can build on
     *
     * @param worker the worker to build with
     * @return a list of <i>buildable</i> cells
     */
    List<Cell> getBuildableCells(Worker worker);

    /**
     * Provides the possible blocks buildable on a given cell
     *
     * @param selectedCell the cell to get the buildable blocks for
     * @return a list of blocks that can be built on the given cell
     */
    List<Block> getBlocks(Cell selectedCell);

    /**
     * Provides a list of cells on which the worker can walk on
     *
     * @param worker the worker to be moved
     * @return a list of <i>walkable</i> cells
     */
    List<Cell> getWalkableCells(Worker worker);

    /**
     * <i>game</i> setter
     *
     * @param game the game in which the effect is used
     */
    void setGame(Game game);

    /**
     * Determines whether a player can end its turn
     *
     * @return true if the player can end its turn, false otherwise
     */
    boolean canEndTurn();

    /**
     * Determines whether a player can end its turn
     * <p>
     * This method should never be invoked directly from the player
     *
     * @return true if the player can end its turn, false otherwise
     */
    boolean canEndTurnAutomatically();

    /**
     * Creates a clone of this object
     *
     * @param game the current game
     * @return a clone of this object
     */
    RuleSetStrategy cloneStrategy(Game game);

}
