package it.polimi.ingsw.model.godCardsEffects.movementEffects;

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

    void opponentAction(MoveAction action){
        if (action.getTargetCell().getOccupiedBy()!= null) {
            Cell pushCell = game.getGameBoard().getCellBehind(action.getStartingCell(), action.getTargetCell()); //Assign to pushCell the Cell that's "behind" the opponent
            moveOpponentWorker(action, pushCell);
        }
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {

           if(super.isMoveActionValid(action)){
                opponentAction(action);
                return true;
           }
           return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (movesAvailable > 0) {
            for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {

                if (canGo(worker, cell)) {
                    if ((cell.getOccupiedBy() == null) || (
                            cell.getOccupiedBy() != null &&
                                    canPush(worker.getPosition(), cell) &&
                                    isNotSameOwner(cell))) {

                        cells.add(cell);
                    }
                }

            }
        }
        return cells;
    }

}
