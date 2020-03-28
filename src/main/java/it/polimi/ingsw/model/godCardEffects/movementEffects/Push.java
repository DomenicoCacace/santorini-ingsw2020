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
        System.out.println("Sono in Push");
        return super.isMoveActionValid(action);
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        for(Cell cell: game.getGameBoard().getAllCells()) {
            if (worker.getPosition().calculateDistance(cell) == 1 &&
                    //TODO: check rules, might push workers further than one cell
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
