package it.polimi.ingsw.model.godCardEffects.movementEffects;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

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
