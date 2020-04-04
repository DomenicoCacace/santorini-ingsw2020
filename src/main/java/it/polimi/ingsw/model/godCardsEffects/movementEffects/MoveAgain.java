package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class MoveAgain extends MovementStrategy {

    private Cell startingCell;

    public MoveAgain() {
        movesAvailable = 2;
        buildsAvailable = 1;
        hasMovedUp= false;
        this.movedWorker= null;
        movesUpAvailable = 2;
    }

    @Override
    public void doEffect() {
        movesAvailable = 2;
        buildsAvailable = 1;
        hasMovedUp= false;
        this.movedWorker= null;
        movesUpAvailable = 2;
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        int x, y;
        if (movedWorker == null && super.isMoveActionValid(action)) {
            x = action.getTargetWorker().getPosition().getCoordX();
            y = action.getTargetWorker().getPosition().getCoordY();
            startingCell = game.getGameBoard().getCell(x, y);
            return true;
        }
        else if (movedWorker == action.getTargetWorker())
            return super.isMoveActionValid(action);
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> adjacentCells = super.getWalkableCells(worker);
        if(movedWorker != null)
            adjacentCells.remove(startingCell);
        return adjacentCells;
    }
}
