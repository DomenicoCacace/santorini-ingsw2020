package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class Swap extends MovementStrategy {

    public void opponentAction(MoveAction action){
        if (action.getTargetCell().getOccupiedBy()!= null) {
            Cell myPreviousCell = action.getStartingCell();
            moveOpponentWorker(action, myPreviousCell);
        }
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if(movesAvailable>0 && isInsideWalkableCells(action)){
            opponentAction(action);
            movesAvailable--;
            if(movesUpAvailable>0)
                movesUpAvailable--;

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
        if(movesAvailable > 0) {
            for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
                if (canGo(worker, cell))
                    if (cell.getOccupiedBy() == null || cell.getOccupiedBy().getOwner() != worker.getOwner())
                        cells.add(cell);
            }
        }
        return cells;
    }
}
