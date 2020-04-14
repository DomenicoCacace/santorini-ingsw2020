package it.polimi.ingsw.model.godCardsEffects.winConditionEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

public class Down2Levels extends WinConditionStrategy {

    public Down2Levels(){super();}

    private Down2Levels(Down2Levels down2levels, Game game) {
        this.game = game;
        this.movesAvailable = down2levels.getMovesAvailable();
        this.movesUpAvailable = down2levels.getMovesUpAvailable();
        this.buildsAvailable = down2levels.getBuildsAvailable();
        this.hasMovedUp = down2levels.hasMovedUp();
        if(down2levels.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(down2levels.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    @Override
    public boolean checkWinCondition(MoveAction action) {

        return (super.checkWinCondition(action)) || (
                        action.getStartingCell().heightDifference(action.getTargetCell()) <= -2);
    }

    @Override
    public RuleSetStrategy getClone(Game game) {
        return new Down2Levels(this, game);
    }

}
