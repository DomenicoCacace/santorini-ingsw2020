package it.polimi.ingsw.model.rules;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.utilities.Memento;

import java.util.List;

public class RuleSetContext implements Memento<RuleSetContext> {

    private RuleSetStrategy strategy;


    public void setGame(Game game) {
        strategy.setGame(game);
    }

    public boolean validateMoveAction(MoveAction action) { return strategy.isMoveActionValid(action); }

    public boolean validateBuildAction(BuildAction action) {
        return strategy.isBuildActionValid(action);
    }

    public RuleSetStrategy getStrategy() {
        return strategy;
    } // TODO: just for testing

    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean canEndTurn(){
        return strategy.canEndTurn();
    }

    public boolean canEndTurnAutomatically(){
        return strategy.canEndTurnAutomatically();
    }

    public void doEffect() { this.strategy.doEffect(); }

    public boolean checkWinCondition(MoveAction action) {
        return strategy.checkWinCondition(action);
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
