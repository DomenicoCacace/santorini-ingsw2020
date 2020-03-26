package it.polimi.ingsw.model;

import java.util.List;

public class RuleSetContext {

    private RuleSetStrategy strategy;

    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }

    public void doEffect(Turn turn) { }

    public int propagateEffect() { return 0;}

    public boolean validateMoveAction(Action action) {
        strategy.isMoveActionValid(action);
        return false;
    }

    public boolean validateBuildAction(Action action) { return false;}

    public boolean checkWinCondition(Action action) { return false;}

    public List<Cell> getWalkableCells(Worker worker) { return strategy.getWalkableCells(worker); }

    public List<Cell> getBuildableCells(Worker worker) { return null; }
}
