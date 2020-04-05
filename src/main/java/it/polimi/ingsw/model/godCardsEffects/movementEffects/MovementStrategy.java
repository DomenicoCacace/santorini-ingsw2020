package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;

public class MovementStrategy extends RuleSetBase {

    public void moveOpponentWorker(MoveAction action, Cell afterCell) {
        Action opponentMoveAction = new MoveAction(action.getTargetCell().getOccupiedBy(), afterCell);
        opponentMoveAction.apply();
    }

    public boolean canGo(Worker worker, Cell cell) {
        return !cell.hasDome() && super.isCorrectDistance(worker, cell);
    }
}
