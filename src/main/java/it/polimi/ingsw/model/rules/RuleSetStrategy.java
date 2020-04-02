package it.polimi.ingsw.model.rules;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.List;

public interface RuleSetStrategy {
    void doEffect(Turn turn);

    @Deprecated
    int propagateEffect();

    int getMovesAvailable();

    boolean hasMovedUp();

    int getBuildsAvailable();

    Worker getMovedWorker();

    boolean isMoveActionValid(MoveAction action);

    boolean isBuildActionValid(BuildAction action);

    boolean checkWinCondition(MoveAction action);

    boolean checkLoseCondition(MoveAction action);

    boolean checkLoseCondition();

    List<Cell> getWalkableCells(Worker worker);

    List<Cell> getBuildableCells(Worker worker);

    void setGame(Game game);
}
