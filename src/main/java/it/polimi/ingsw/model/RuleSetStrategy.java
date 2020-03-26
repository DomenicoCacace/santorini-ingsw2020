package it.polimi.ingsw.model;

import java.util.List;

public interface RuleSetStrategy {
    void doEffect(Turn turn);
    int propagateEffect();
    boolean isMoveActionValid(Action action);
    boolean isBuildActionValid(Action action);
    boolean checkWinCondition(Action action);
    List<Cell> getWalkableCells(Worker worker);
    List<Cell> getBuildableCells(Worker worker);
}
