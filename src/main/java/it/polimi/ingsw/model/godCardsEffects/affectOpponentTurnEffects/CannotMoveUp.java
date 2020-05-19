package it.polimi.ingsw.model.godCardsEffects.affectOpponentTurnEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

/**
 * If a worker of a player using this effects moves on an higher level, all the other players cannot move on an higher
 * level during their next turn. If an opponent can only move up and this effect is active, it automatically loses.
 */
public class CannotMoveUp extends AffectOpponentTurnStrategy {

    /**
     * Default constructor
     * @see AffectOpponentTurnStrategy#initialize()
     */
    public CannotMoveUp() {
        super();
    }

    /**
     * Copy constructor
     * @param cannotMoveUp the strategy to clone
     * @param game the game in which the effect is used
     */
    private CannotMoveUp(CannotMoveUp cannotMoveUp, Game game) {
        this.game = game;
        this.movesAvailable = cannotMoveUp.getMovesAvailable();
        this.movesUpAvailable = cannotMoveUp.getMovesUpAvailable();
        this.buildsAvailable = cannotMoveUp.getBuildsAvailable();
        this.hasMovedUp = cannotMoveUp.hasMovedUp();
        if (cannotMoveUp.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(cannotMoveUp.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    /**
     * Applies the end turn effect
     * <p>
     *     Using this ruleSet, the end turn effects reset the attributes changed during the turn and, if a worker has
     *     moved up during the turn, all of the other workers cannot move on a taller building during their next turn
    */
    @Override
    public void doEffect() {
        if (hasMovedUp) {
            for (Player players : game.getPlayers()) {
                if (game.getCurrentTurn().getCurrentPlayer() != players)
                    players.getGod().getStrategy().setMovesUpAvailable(0);
            }
        }
        super.doEffect();
    }

    /**
     * Creates a clone of this object
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new CannotMoveUp(this, game);
    }

}
