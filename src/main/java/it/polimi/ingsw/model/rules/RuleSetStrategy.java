package it.polimi.ingsw.model.rules;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.List;

public interface RuleSetStrategy {
    void doEffect();

    void setMovesUpAvailable(int num); //Testing purpose only

    int getMovesUpAvailable(); //Testing purpose only

    int getMovesAvailable(); //Testing purpose only

    boolean hasMovedUp(); //Testing purpose only

    int getBuildsAvailable(); //Testing purpose only

    Worker getMovedWorker();//Testing purpose only

    boolean isInsideWalkableCells(MoveAction action);

    boolean isCorrectDistance(Worker worker, Cell cell);

    boolean isMoveActionValid(MoveAction action);

    void addWalkableCells(Worker worker, List<Cell> cells);

    boolean canBuild(BuildAction action);

    boolean isCorrectBlock(BuildAction action);

    boolean isInsideBuildableCells(BuildAction action);

    boolean isBuildActionValid(BuildAction action);

    void addBuildableCells(Worker worker, List<Cell> cells);

    boolean checkWinCondition(MoveAction action);

    boolean checkLoseCondition(MoveAction action);

    boolean checkLoseCondition();

    List<Cell> getWalkableCells(Worker worker);

    List<Cell> getBuildableCells(Worker worker);

    void setGame(Game game);
}
