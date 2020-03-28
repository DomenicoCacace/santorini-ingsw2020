package it.polimi.ingsw.model.godCardEffects.winConditionEffects;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.ActionType;

public class Down2Levels extends WinConditionStrategy {

    @Override
    public boolean checkWinCondition(Action action) {

        return (super.checkWinCondition(action)) || (
                action.getType() == ActionType.MOVE &&
                action.getStartingCell().heightDifference(action.getTargetCell()) <= -2);
    }
}
