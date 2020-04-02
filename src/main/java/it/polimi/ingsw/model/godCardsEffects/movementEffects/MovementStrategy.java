package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.rules.RuleSetBase;

import java.util.List;

public class MovementStrategy extends RuleSetBase {

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        return super.isMoveActionValid(action);
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        return super.getWalkableCells(worker);
    }
}
