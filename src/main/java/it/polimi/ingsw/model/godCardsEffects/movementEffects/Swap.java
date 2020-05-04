package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Swap extends MovementStrategy {

    private final List<Worker> stuckWorkers;

    public Swap() {
        super();
        stuckWorkers = new ArrayList<>();
    }

    private Swap(Swap swap, Game game) {
        this.game = game;
        this.movesAvailable = swap.getMovesAvailable();
        this.movesUpAvailable = swap.getMovesUpAvailable();
        this.buildsAvailable = swap.getBuildsAvailable();
        this.hasMovedUp = swap.hasMovedUp();
        if (swap.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(swap.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        this.stuckWorkers=swap.stuckWorkers;
    }

    public void swapAction(MoveAction action) {
        if (action.getTargetCell().getOccupiedBy() != null) {

            Cell myPreviousCell = action.getStartingCell();
            Cell myAfterCell = action.getTargetCell();
            Worker myWorker = action.getTargetWorker();
            Worker opponentWorker = action.getTargetCell().getOccupiedBy();

            if (myWorker.getPosition().heightDifference(myAfterCell) == 1)
                hasMovedUp = true;

            myWorker.setPosition(myAfterCell);
            myAfterCell.setOccupiedBy(myWorker);
            opponentWorker.setPosition(myPreviousCell);
            myPreviousCell.setOccupiedBy(opponentWorker);
        }
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (movesAvailable > 0 && isInsideWalkableCells(action)) {
            movedWorker = action.getTargetWorker();
            swapAction(action);
            movesAvailable--;
            startingCell = action.getStartingCell();
            if (movesUpAvailable > 0)
                movesUpAvailable--;
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        List<Cell> losingCells = new ArrayList<>();
        if (movesAvailable > 0) {
            for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
                if (canGo(worker, cell) && (cell.getOccupiedBy() == null || isNotSameOwner(cell))) { //TODO: let apollo kill himself
                    if (!canBuildOnAtLeastOneCell(cell))
                        losingCells.add(cell);
                    cells.add(cell);
                }
            }
        }
        if(losingCells.size() == cells.size()) {
            if(!stuckWorkers.contains(worker))
                stuckWorkers.add(worker);
            if (stuckWorkers.size()<2)
                cells.removeAll(losingCells);
        }
        else
            cells.removeAll(losingCells);
        return cells;
    }

    private boolean canBuildOnAtLeastOneCell(Cell targetCell) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(targetCell)) {
            if (!cell.hasDome() && cell.getOccupiedBy() == null)
                return true;
        }
        return false;
    }


    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new Swap(this, game);
    }

}