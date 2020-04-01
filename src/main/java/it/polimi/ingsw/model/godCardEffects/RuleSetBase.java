package it.polimi.ingsw.model.godCardEffects;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.ArrayList;
import java.util.List;

public class RuleSetBase implements RuleSetStrategy {

    protected Game game;
    protected int movesAvailable;
    protected boolean hasMovedUp;
    protected int buildsAvailable;
    protected Worker movedWorker;
    private List<Cell> walkableCells;
    private List<Cell> buildableCells;

    public RuleSetBase() {
        this.movesAvailable = 1;
        this.buildsAvailable = 1;
        this.hasMovedUp=false;
    }


    @Override
    public int getMovesAvailable() {
        return movesAvailable;
    }
    @Override
    public boolean hasMovedUp() {
        return hasMovedUp;
    }

    @Override
    public int getBuildsAvailable() {
        return buildsAvailable;
    }

    @Override
    public Worker getMovedWorker() {
        return movedWorker;
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void doEffect(Turn turn) {
        movesAvailable = 1;
        buildsAvailable = 1;
        hasMovedUp= false;
        this.movedWorker= null;
    }

    @Override
    public int propagateEffect() {
        return 0;
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (getWalkableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                this.movesAvailable>0){
            movesAvailable--;
            if(action.getTargetWorker().getPosition().heightDifference(action.getTargetCell()) == 1)
                hasMovedUp=true;
            movedWorker = action.getTargetWorker();
            return true;
        }
        return false;
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1) &&
                this.buildsAvailable>0 && movedWorker == action.getTargetWorker()){
            buildsAvailable--;
            return true;
        }
        return false;

    }

    @Override
    public boolean checkWinCondition(MoveAction action) {
        //removed action.getTargetCell().hasDome()
        return action.getTargetCell().getBlock().getHeight() == 3 &&
                action.getStartingCell().heightDifference(action.getTargetCell()) == 1;
    }

    @Override
    public boolean checkLoseCondition(MoveAction action) {
        return getBuildableCells(action.getTargetWorker()) == null;
    }

    @Override
    public boolean checkLoseCondition() {
        /* calling this function at the beginning of the turn
         * to check if the player can make a move
         */
        int legalCells = 0;
        for (Worker worker : game.getCurrentTurn().getCurrentPlayer().getWorkers())
            legalCells += getWalkableCells(worker).size();

        return legalCells == 0;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if (worker.getPosition().heightDifference(cell) <= 1 && cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }
        return cells;
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if (cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }
        return cells;
    }
}
