package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.rules.RuleSetBase;

public class MovementStrategy extends RuleSetBase {

    protected boolean canGo(Worker worker, Cell cell) {
        return !cell.hasDome() && super.isCorrectDistance(worker, cell);
    }

    protected boolean isNotSameOwner(Cell cell) {
        return !game.getCurrentTurn().getCurrentPlayer().getWorkers().contains(cell.getOccupiedBy());
    }
}