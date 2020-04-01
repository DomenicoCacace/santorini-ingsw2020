package it.polimi.ingsw.model.godCardEffects.movementEffects;

import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class Push extends MovementStrategy {

    boolean canPush(Cell myCell, Cell targetCell) {
       // Cell pushedCell= game.getGameBoard().getCellBehind(myCell, targetCell); debugging purpose
        return game.getGameBoard().getCellBehind(myCell, targetCell) != null &&
                game.getGameBoard().getCellBehind(myCell, targetCell).getOccupiedBy() == null &&
                !game.getGameBoard().getCellBehind(myCell, targetCell).hasDome();
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {

           if(getWalkableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                movesAvailable>0){
                if (action.getTargetCell().getOccupiedBy()!= null) {
                Cell pushCell = game.getGameBoard().getCellBehind(action.getStartingCell(), action.getTargetCell()); //Assign to pushCell the Cell that's "behind" the opponent
                Action pushAction = new MoveAction(action.getTargetCell().getOccupiedBy(), pushCell); //Create a new Action internally, this action should push the opponent
                pushAction.apply();
                }
                movesAvailable--;
                if(action.getTargetWorker().getPosition().heightDifference(action.getTargetCell()) == 1)
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

            if (worker.getPosition().heightDifference(cell) <= 1 &&
                !cell.hasDome()) {

                if ((cell.getOccupiedBy() == null) || (
                        cell.getOccupiedBy() != null &&
                                canPush(worker.getPosition(), cell) &&
                                cell.getOccupiedBy().getOwner() != worker.getOwner())) {

                    cells.add(cell);
                }
            }
        }
        return cells;
    }
}
