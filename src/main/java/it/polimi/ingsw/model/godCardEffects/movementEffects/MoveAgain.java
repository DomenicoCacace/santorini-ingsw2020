package it.polimi.ingsw.model.godCardEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.Worker;

import java.util.List;

public class MoveAgain extends MovementStrategy {

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        System.out.println("sono in MoveAgain");
        return super.isMoveActionValid(action);
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        return super.getWalkableCells(worker);
    }
}
