package it.polimi.ingsw.model.godCardEffects.winConditionEffects;

import it.polimi.ingsw.model.action.MoveAction;

public class Down2Levels extends WinConditionStrategy {

    @Override
    public boolean checkWinCondition(MoveAction action) {

        return (super.checkWinCondition(action)) || (
                        action.getStartingCell().heightDifference(action.getTargetCell()) <= -2);
    }
}
