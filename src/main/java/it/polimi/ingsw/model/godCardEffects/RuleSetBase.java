package it.polimi.ingsw.model.godCardEffects;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class RuleSetBase implements RuleSetStrategy {

    protected Game game;
    private List<Cell> walkableCells;
    private List<Cell> buildableCells;

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void doEffect(Turn turn) {
        //does nothing, gets overrode in AffectOpponentActions classes
    }

    @Override
    public int propagateEffect() {
        return 0;
    }

    @Override
    public boolean isMoveActionValid(Action action) {
        return getWalkableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                !game.getCurrentTurn().getCurrentPlayer().getHasMoved();
    }

    @Override
    public boolean isBuildActionValid(Action action) {
        return getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                !game.getCurrentTurn().getCurrentPlayer().getHasBuilt();
    }

    @Override
    public boolean checkWinCondition(Action action) {
        //removed action.getTargetCell().hasDome()
        return action.getType() == ActionType.MOVE &&
                action.getTargetCell().getBlock().getHeight() == 3 &&
                action.getStartingCell().heightDifference(action.getTargetCell()) == 1;
    }

    @Override
    public boolean checkLoseCondition(Action action) {
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
        for (Cell cell : game.getGameBoard().getAllCells()) {
            if (worker.getPosition().calculateDistance(cell) == 1 && worker.getPosition().heightDifference(cell) <= 1 && cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }
        return cells;
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        for (Cell cell : game.getGameBoard().getAllCells()) {
            if (worker.getPosition().calculateDistance(cell) == 1 && cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }
        return cells;
    }
}
