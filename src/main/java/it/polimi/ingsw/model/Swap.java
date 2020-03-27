package it.polimi.ingsw.model;

import java.util.List;

public class Swap extends MovementStrategy {


    @Override
    public boolean isMoveActionValid(Action action) {
        System.out.println("Sono in Swap");
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        return super.getWalkableCells(worker);
    }
}
