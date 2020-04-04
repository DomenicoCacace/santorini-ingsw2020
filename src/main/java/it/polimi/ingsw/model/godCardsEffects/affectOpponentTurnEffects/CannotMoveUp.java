package it.polimi.ingsw.model.godCardsEffects.affectOpponentTurnEffects;

import it.polimi.ingsw.model.Player;

public class CannotMoveUp extends AffectOpponentTurnStrategy {

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
    public int propagateEffect() {
        return super.propagateEffect();
    }
}
