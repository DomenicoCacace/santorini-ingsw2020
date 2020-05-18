package it.polimi.ingsw.model.godCardsEffects.winConditionEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

/**
 * Jump down two levels to win
 * <p>
 *     This effect alters the player win conditions: the default rule (move on a level 3 structure) still applies, and
 *     an additional one is added: if a worker performs a movement action between two cells which height difference is
 *     at least 2 (only going from a taller building to a lower one), the player immediately wins. The movement must be
 *     performed by the worker (<i>forced is not moved</i>)
 * </p>
 */
public class Down2Levels extends WinConditionStrategy {

    /**
     * Default constructor
     * @see #initialize()
     */
    public Down2Levels() {
        super();
    }

    /**
     * Copy constructor
     * @param down2levels the strategy to clone
     * @param game the game in which the effect is used
     */
    private Down2Levels(Down2Levels down2levels, Game game) {
        this.game = game;
        this.movesAvailable = down2levels.getMovesAvailable();
        this.movesUpAvailable = down2levels.getMovesUpAvailable();
        this.buildsAvailable = down2levels.getBuildsAvailable();
        this.hasMovedUp = down2levels.hasMovedUp();
        if (down2levels.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(down2levels.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    /**
     * Determines if the win conditions are satisfied upon a movement action
     * <p>
     *     Using this ruleSet, a player can win upon moving (following the rules already defined in
     *     {@linkplain #isMoveActionValid(MoveAction)}) on a level 3 building OR upon jumping to a cell at least two
     *     levels shorter than the starting block
     * </p>
     * @param action the action to analyze
     * @return true if the action led to victory, false otherwise
     */
    @Override
    public boolean checkWinCondition(MoveAction action) {

        return (super.checkWinCondition(action)) || (
                startingCell.heightDifference(action.getTargetCell()) <= -2);
    }

    /**
     * Creates a clone of this object
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new Down2Levels(this, game);
    }

}
