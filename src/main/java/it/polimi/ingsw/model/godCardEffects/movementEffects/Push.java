package it.polimi.ingsw.model.godCardEffects.movementEffects;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class Push extends MovementStrategy {

    boolean canPush(Cell myCell, Cell targetCell) {
        return game.getGameBoard().getCellBehind(myCell, targetCell) != null &&
                game.getGameBoard().getCellBehind(myCell, targetCell).getOccupiedBy() == null &&
                !game.getGameBoard().getCellBehind(myCell, targetCell).hasDome();
    }

    @Override
    public boolean isMoveActionValid(Action action) {
        if (super.isMoveActionValid(action)){ //if the worker can move in the target cell (checked by walkableCells)
            if (action.getTargetCell().getOccupiedBy()!= null) {
                Cell pushCell = game.getGameBoard().getCellBehind(action.getStartingCell(), action.getTargetCell()); //Assign to pushCell the Cell that's "behind" the opponent
                Action pushAction = new Action(action.getTargetCell().getOccupiedBy(), pushCell); //Create a new Action internally, this action should push the opponent
                pushAction.applier();
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        for (Cell cell : game.getGameBoard().getAllCells()) {
            if (worker.getPosition().calculateDistance(cell) == 1 &&
                    /*TODO: check rules, might push workers further than one cell
                     * or push workers higher than one level
                     */
                    worker.getPosition().heightDifference(cell) <= 1 &&
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
