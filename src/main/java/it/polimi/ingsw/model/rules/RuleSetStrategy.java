package it.polimi.ingsw.model.rules;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="stratId", scope = RuleSetStrategy.class)

public interface RuleSetStrategy {

    void doEffect();

    int getMovesUpAvailable(); //Testing purpose only

    int getMovesAvailable(); //Testing purpose only

    boolean hasMovedUp(); //Testing purpose only

    int getBuildsAvailable(); //Testing purpose only

    Worker getMovedWorker(); //Testing purpose only

    void setMovesUpAvailable(int num);

    boolean isMoveActionValid(MoveAction action);

    boolean isBuildActionValid(BuildAction action);

    boolean checkWinCondition(MoveAction action);

    boolean checkLoseCondition(MoveAction action);

    boolean checkLoseCondition();

    List<Cell> getWalkableCells(Worker worker);

    List<Cell> getBuildableCells(Worker worker);

    void setGame(Game game);

    boolean canEndTurn();

    boolean canEndTurnAutomatically();

}
