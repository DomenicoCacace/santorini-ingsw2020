package it.polimi.ingsw.model;

import java.util.List;

public class RuleSetContext implements Memento<RuleSetContext> {

    private RuleSetStrategy strategy;

    public void setGame(Game game){
        strategy.setGame(game);
    }

    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }

    public RuleSetStrategy getStrategy() {
        return strategy;
    }

    public void doEffect(Turn turn) { }

    public int propagateEffect() { return 0;}

    public boolean validateMoveAction(Action action) {
        strategy.isMoveActionValid(action);
        return false;
    }

    public boolean validateBuildAction(Action action) {
        strategy.isBuildActionValid(action);
        return false;}

    public boolean checkWinCondition(Action action) { return false;}

    public List<Cell> getWalkableCells(Worker worker) { return strategy.getWalkableCells(worker); }

    public List<Cell> getBuildableCells(Worker worker) { return null; }

    @Override
    public RuleSetContext saveState() {

        RuleSetContext savedContext = new RuleSetContext();
        savedContext.setStrategy(this.strategy);

        return savedContext;
    }

    @Override
    public void restoreState(RuleSetContext savedState) {
        this.strategy = savedState.getStrategy();
    }
}
