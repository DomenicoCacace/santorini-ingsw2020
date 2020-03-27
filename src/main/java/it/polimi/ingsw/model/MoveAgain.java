package it.polimi.ingsw.model;

import java.util.List;

public class MoveAgain extends MovementStrategy {

    @Override
    public boolean isMoveActionValid(Action action) {
        System.out.println("sono in MoveAgain");
        return super.isMoveActionValid(action);
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        return super.getWalkableCells(worker);
    }
}
