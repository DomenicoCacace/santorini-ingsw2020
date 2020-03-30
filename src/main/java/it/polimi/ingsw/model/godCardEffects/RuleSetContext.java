package it.polimi.ingsw.model.godCardEffects;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.utilities.Memento;

import java.util.List;

public class RuleSetContext implements Memento<RuleSetContext> {

    private RuleSetStrategy strategy;


    public void setGame(Game game) {
        strategy.setGame(game);
    }

    public boolean validateMoveAction(Action action) {
        return strategy.isMoveActionValid(action);
        //it returned false
    }

    public boolean validateBuildAction(Action action) {
        return strategy.isBuildActionValid(action);
    }

    public RuleSetStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }

    public void doEffect(Turn turn) {

    }

    public int propagateEffect() {
        return 0;
    }

    public boolean checkWinCondition(Action action) {
        return strategy.checkWinCondition(action);
    }

    public boolean checkLoseCondition(Action action) {
        return strategy.checkLoseCondition(action);
    }

    public boolean checkLoseCondition() {
        return strategy.checkLoseCondition();
    }

    public List<Cell> getWalkableCells(Worker worker) {
        return strategy.getWalkableCells(worker);
    }

    public List<Cell> getBuildableCells(Worker worker) {
        return strategy.getBuildableCells(worker);
    }

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
