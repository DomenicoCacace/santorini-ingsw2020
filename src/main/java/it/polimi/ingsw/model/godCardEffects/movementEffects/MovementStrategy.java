package it.polimi.ingsw.model.godCardEffects.movementEffects;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;

import java.util.List;

public class MovementStrategy extends RuleSetBase {

    @Override
    public boolean isMoveActionValid(Action action) {
        return super.isMoveActionValid(action);
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        return super.getWalkableCells(worker);
    }
}
