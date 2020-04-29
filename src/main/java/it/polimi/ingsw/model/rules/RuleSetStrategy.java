package it.polimi.ingsw.model.rules;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "stratId", scope = RuleSetStrategy.class)

public interface RuleSetStrategy {

    void doEffect();

    int getMovesUpAvailable(); //Testing purpose only

    void setMovesUpAvailable(int num);

    int getMovesAvailable(); //Testing purpose only

    boolean hasMovedUp(); //Testing purpose only

    int getBuildsAvailable(); //Testing purpose only

    Worker getMovedWorker(); //Testing purpose only

    List<PossibleActions> getPossibleActions(Worker worker);

    boolean isMoveActionValid(MoveAction action);

    boolean isBuildActionValid(BuildAction action);

    boolean checkWinCondition(MoveAction action);

    boolean checkLoseCondition();

    List<Cell> getBuildableCells(Worker worker);

    List<Block> getBlocks(Cell selectedCell);

    List<Cell> getWalkableCells(Worker worker);

    void setGame(Game game);

    boolean canEndTurn();

    boolean canEndTurnAutomatically();

    RuleSetStrategy cloneStrategy(Game game);

}
