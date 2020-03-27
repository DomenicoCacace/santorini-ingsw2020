package it.polimi.ingsw.model;

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
