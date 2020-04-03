package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Turn;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.Worker;

import java.util.List;

public class MoveAgain extends MovementStrategy {

    private Cell startingCell;

    public MoveAgain() {
        movesAvailable = 2;
        buildsAvailable = 1;
        hasMovedUp= false;
        this.movedWorker= null;
    }

    @Override
    public void doEffect(Turn turn) {
        movesAvailable = 2;
        buildsAvailable = 1;
        hasMovedUp= false;
        this.movedWorker= null;
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if((movedWorker == null) || movedWorker == action.getTargetWorker())
            return super.isMoveActionValid(action);
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        if(this.movesAvailable>0) {
            int x, y;
            if (startingCell == null) {
                x = worker.getPosition().getCoordX();
                y = worker.getPosition().getCoordY();
                startingCell = game.getGameBoard().getCell(x, y);
                return super.getWalkableCells(worker);
            }
            List<Cell> adjacentCells = super.getWalkableCells(worker);
            adjacentCells.remove(startingCell);
            return adjacentCells;
        }

        return null;
    }
}
