package it.polimi.ingsw.model.godCardEffects;

import it.polimi.ingsw.model.*;

import java.util.List;

public interface RuleSetStrategy {
    void doEffect(Turn turn);

    @Deprecated
    int propagateEffect();

    boolean isMoveActionValid(Action action);

    boolean isBuildActionValid(Action action);

    boolean checkWinCondition(Action action);

    boolean checkLoseCondition(Action action);

    List<Cell> getWalkableCells(Worker worker);

    List<Cell> getBuildableCells(Worker worker);

    boolean checkLoseCondition();

    void setGame(Game game);
}
