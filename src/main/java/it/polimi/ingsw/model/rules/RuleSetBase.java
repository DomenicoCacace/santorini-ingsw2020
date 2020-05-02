package it.polimi.ingsw.model.rules;


import com.fasterxml.jackson.annotation.JsonIgnore;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.ArrayList;
import java.util.List;



public class RuleSetBase implements RuleSetStrategy {

    @JsonIgnore
    protected Game game;

    protected int movesAvailable;
    protected int movesUpAvailable;
    protected int buildsAvailable;
    protected boolean hasMovedUp;
    protected Cell startingCell;
    protected Worker movedWorker;

    public RuleSetBase() {
        initialize();
    }

    public RuleSetBase(RuleSetStrategy ruleSetBase, Game game) {
        this.game = game;
        this.movesAvailable = ruleSetBase.getMovesAvailable();
        this.movesUpAvailable = ruleSetBase.getMovesUpAvailable();
        this.buildsAvailable = ruleSetBase.getBuildsAvailable();
        this.hasMovedUp = ruleSetBase.hasMovedUp();
        if (ruleSetBase.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(ruleSetBase.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    protected void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 1;
        this.hasMovedUp = false;
        this.movedWorker = null;
    }

    @Override
    public int getMovesAvailable() {
        return movesAvailable;
    }

    @Override
    public int getMovesUpAvailable() {
        return movesUpAvailable;
    }

    @Override
    public void setMovesUpAvailable(int num) {
        this.movesUpAvailable = num;
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
    public List<Block> getBlocks(Cell selectedCell) {
        List<Block> buildingBlocks = new ArrayList<>();
        buildingBlocks.add(Block.values()[selectedCell.getBlock().getHeight() + 1]);
        return buildingBlocks;
    }

    @Override
    public void doEffect() {
        initialize();
    }


    protected boolean isInsideWalkableCells(MoveAction action) {
        return getWalkableCells(action.getTargetWorker()).contains(action.getTargetCell());

    }

    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = new ArrayList<>();
        if (getWalkableCells(worker).size() > 0) {
            possibleActions.add(PossibleActions.MOVE);
            possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);
        } else if (getBuildableCells(worker).size() > 0) {
            possibleActions.add(PossibleActions.BUILD);
        } else
            possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);
        return possibleActions;


    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (isInsideWalkableCells(action)) {
            movesAvailable--;

            if (movesUpAvailable > 0)
                movesUpAvailable--;

            if (action.getTargetWorker().getPosition().heightDifference(action.getTargetCell()) == 1)
                hasMovedUp = true;
            movedWorker = action.getTargetWorker();
            startingCell = action.getStartingCell();
            return true;
        }
        return false;
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (canBuild(action)) {
            buildsAvailable--;
            movesAvailable = 0;
            movesUpAvailable = 0;
            return true;
        }
        return false;
    }


    protected boolean isInsideBuildableCells(BuildAction action) {
        return getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell());
    }

    protected boolean canBuild(BuildAction action) {
        return isInsideBuildableCells(action) && isCorrectBlock(action) &&
                movedWorker == action.getTargetWorker();
    }


    protected boolean isCorrectBlock(BuildAction action) {
        return action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1);
    }

    @Override
    public boolean checkWinCondition(MoveAction action) {
        return action.getTargetCell().getBlock().getHeight() == 3 &&
                startingCell.heightDifference(action.getTargetCell()) == 1;
    }

    @Override
    public boolean checkLoseCondition(MoveAction moveAction){
        return (getBuildableCells(moveAction.getTargetWorker()).size()==0);
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
        if (movesAvailable > 0) {
            addWalkableCells(worker, cells);
        }
        return cells;
    }


    protected void addWalkableCells(Worker worker, List<Cell> cells) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if ((!cell.hasDome() && cell.getOccupiedBy() == null) && isCorrectDistance(worker, cell))
                cells.add(cell);
        }
    }


    protected boolean isCorrectDistance(Worker worker, Cell cell) {
        return (worker.getPosition().heightDifference(cell) <= 0) || (worker.getPosition().heightDifference(cell) == 1 && movesUpAvailable > 0);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (buildsAvailable > 0) {
            if (worker.equals(movedWorker)) {
                addBuildableCells(worker, cells);
            }
        }
        return cells;
    }


    protected void addBuildableCells(Worker worker, List<Cell> cells) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if (cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }
    }

    @Override
    public boolean canEndTurn() {
        return canEndTurnAutomatically();
    }

    @Override
    public boolean canEndTurnAutomatically() {
        return (movesAvailable == 0 && buildsAvailable == 0);
    }

    public RuleSetStrategy cloneStrategy(Game game) {
        return new RuleSetBase(this, game);
    }


}
