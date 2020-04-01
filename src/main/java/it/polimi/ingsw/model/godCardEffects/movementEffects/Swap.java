package it.polimi.ingsw.model.godCardEffects.movementEffects;

import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class Swap extends MovementStrategy {


    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if(getWalkableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                movesAvailable>0){
            if (action.getTargetCell().getOccupiedBy()!= null) {
                Cell myPreviousCell = action.getStartingCell();
                Action opponentOnMyPreviousCellAction = new MoveAction(action.getTargetCell().getOccupiedBy(), myPreviousCell);
                opponentOnMyPreviousCellAction.apply();
                // perhaps we need a temp cell (2 workers cannot stay at the same time on the same cell)
            }
            movesAvailable--;
            if(action.getTargetCell().heightDifference(action.getTargetWorker().getPosition()) == 1)
                hasMovedUp=true;
            movedWorker = action.getTargetWorker();
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if (worker.getPosition().heightDifference(cell) <= 1 && !cell.hasDome())
                if(cell.getOccupiedBy() == null || cell.getOccupiedBy().getOwner() != worker.getOwner())
                    cells.add(cell);
        }
        return cells;
    }
}
