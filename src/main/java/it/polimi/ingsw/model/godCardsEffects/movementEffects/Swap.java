package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

public class Swap extends MovementStrategy {

    public Swap(){super();}

    private Swap(Swap swap, Game game){
        this.game = game;
        this.movesAvailable = swap.getMovesAvailable();
        this.movesUpAvailable = swap.getMovesUpAvailable();
        this.buildsAvailable = swap.getBuildsAvailable();
        this.hasMovedUp = swap.hasMovedUp();
        if(swap.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(swap.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    public void swapAction(MoveAction action){
        if (action.getTargetCell().getOccupiedBy()!= null) {

            Cell myPreviousCell = action.getStartingCell();
            Cell myAfterCell = action.getTargetCell();
            Worker myWorker = action.getTargetWorker();
            Worker opponentWorker = action.getTargetCell().getOccupiedBy();

            if(myWorker.getPosition().heightDifference(myAfterCell) == 1)
                hasMovedUp=true;

            myWorker.setPosition(myAfterCell);
            myAfterCell.setOccupiedBy(myWorker);
            opponentWorker.setPosition(myPreviousCell);
            myPreviousCell.setOccupiedBy(opponentWorker);
        }
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if(movesAvailable>0 && isInsideWalkableCells(action)){
            movedWorker = action.getTargetWorker();
            swapAction(action);
            movesAvailable--;
            if(movesUpAvailable>0)
                movesUpAvailable--;
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if(movesAvailable > 0) {
            for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
                if (canGo(worker, cell) && canBuildOnAtLeastOneCell(cell))
                    if (cell.getOccupiedBy() == null || isNotSameOwner(cell))
                        cells.add(cell);
            }
        }
        return cells;
    }

    private boolean canBuildOnAtLeastOneCell(Cell targetCell){
        for(Cell cell: game.getGameBoard().getAdjacentCells(targetCell)){
            if(!cell.hasDome() && cell.getOccupiedBy() == null)
                return true;
        }
        return false;
    }



    @Override
    public RuleSetStrategy getClone(Game game) {
        return new Swap(this, game);
    }

}