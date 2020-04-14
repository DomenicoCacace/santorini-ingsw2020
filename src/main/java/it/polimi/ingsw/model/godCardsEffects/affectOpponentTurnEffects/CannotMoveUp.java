package it.polimi.ingsw.model.godCardsEffects.affectOpponentTurnEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

public class CannotMoveUp extends AffectOpponentTurnStrategy {

    public CannotMoveUp(){super();}

    private CannotMoveUp(CannotMoveUp cannotMoveUp, Game game){
        this.game = game;
        this.movesAvailable = cannotMoveUp.getMovesAvailable();
        this.movesUpAvailable = cannotMoveUp.getMovesUpAvailable();
        this.buildsAvailable = cannotMoveUp.getBuildsAvailable();
        this.hasMovedUp = cannotMoveUp.hasMovedUp();
        if(cannotMoveUp.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(cannotMoveUp.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    @Override
    public void doEffect() {
        if (hasMovedUp) {
            for (Player players : game.getPlayers()) {
                if(game.getCurrentTurn().getCurrentPlayer() != players)
                    players.getGod().getStrategy().setMovesUpAvailable(0);
            }
        }
        super.doEffect();
    }

    @Override
    public RuleSetStrategy getClone(Game game) {
        return new CannotMoveUp(this, game);
    }

}
