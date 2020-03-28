package it.polimi.ingsw.model;

import java.util.List;

public class Push extends MovementStrategy {

    @Override
    public boolean isMoveActionValid(Action action) {
        System.out.println("Sono in Push");
        return super.isMoveActionValid(action);
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        return super.getWalkableCells(worker);
    }
}
