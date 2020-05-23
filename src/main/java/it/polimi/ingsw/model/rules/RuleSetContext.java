package it.polimi.ingsw.model.rules;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.List;

/**
 * Context class for the implementation of the <a href=https://en.wikipedia.org/wiki/Strategy_pattern>Strategy pattern</a>
 * on the various gods effects
 */
public class RuleSetContext {

    private RuleSetStrategy strategy;

    /**
     * <i>game</i> setter
     *
     * @param game the game in which the effect is used
     */
    public void setGame(Game game) {
        strategy.setGame(game);
    }

    /**
     * Determines if a moveAction is legal and applies it
     *
     * @param action the movement action to validate
     * @return true if the action has been applied, false otherwise
     */
    public boolean validateMoveAction(MoveAction action) {
        return strategy.isMoveActionValid(action);
    }

    /**
     * Determines if a buildAction is legal and applies it
     *
     * @param action the build action to validate
     * @return true if the action has been applied, false otherwise
     */
    public boolean validateBuildAction(BuildAction action) {
        return strategy.isBuildActionValid(action);
    }

    /**
     * <i>strategy</i> getter
     *
     * @return the current strategy
     */
    public RuleSetStrategy getStrategy() {
        return strategy;
    } // TODO: just for testing

    /**
     * <i>strategy</i> setter
     *
     * @param strategy the strategy to set
     */
    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Determines whether a player can end its turn
     *
     * @return true if the player can end its turn, false otherwise
     */
    public boolean canEndTurn() {
        return strategy.canEndTurn();
    }

    /**
     * Determines whether a player can end its turn
     *
     * @return true if the player can end its turn, false otherwise
     */
    public boolean canEndTurnAutomatically() {
        return strategy.canEndTurnAutomatically();
    }

    /**
     * Applies end turn effects
     */
    public void doEffect() {
        this.strategy.doEffect();
    }

    /**
     * Determines if the win conditions are satisfied upon a movement action
     *
     * @param action the action to analyze
     * @return true if the action led to victory, false otherwise
     */
    public boolean checkWinCondition(MoveAction action) {
        return strategy.checkWinCondition(action);
    }

    /**
     * Determines if the lose conditions are satisfied upon a movement action
     *
     * @param action the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    public boolean checkLoseCondition(MoveAction action) {
        return strategy.checkLoseCondition(action);
    }

    /**
     * Determines if the lose conditions are satisfied upon a movement action
     *
     * @param action the action to analyze
     * @return true if the action led to a loss, false otherwise
     */
    public boolean checkLoseCondition(BuildAction action) {
        return strategy.checkLoseCondition(action);
    }

    /**
     * Checks if the turn can begin, checking for both players to be <i>free</i>
     *
     * @return true if there is at least one action to perform, false otherwise
     */
    public boolean checkLoseCondition() {
        return strategy.checkLoseCondition();
    }

    /**
     * Provides a list of cells on which the worker can walk on
     *
     * @param worker the worker to be moved
     * @return a list of <i>walkable</i> cells
     */
    public List<Cell> getWalkableCells(Worker worker) {
        return strategy.getWalkableCells(worker);
    }

    /**
     * Provides a list of cells on which the worker can build on
     *
     * @param worker the worker to build with
     * @return a list of <i>buildable</i> cells
     */
    public List<Cell> getBuildableCells(Worker worker) {
        return strategy.getBuildableCells(worker);
    }

}
