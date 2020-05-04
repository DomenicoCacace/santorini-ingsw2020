package it.polimi.ingsw.model.rules;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects.BuildBeforeAfterMovement;
import it.polimi.ingsw.model.godCardsEffects.affectOpponentTurnEffects.CannotMoveUp;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainDifferentCell;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainSameCell;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.MoveAgain;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Swap;
import it.polimi.ingsw.model.godCardsEffects.winConditionEffects.Down2Levels;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Effect Name")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "stratId", scope = RuleSetStrategy.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleSetBase.class, name="RuleSetBase"),
        @JsonSubTypes.Type(value = Push.class, name="Push"),
        @JsonSubTypes.Type(value = Swap.class, name="Swap"),
        @JsonSubTypes.Type(value = MoveAgain.class, name="Move Again"),
        @JsonSubTypes.Type(value = BuildAgainDifferentCell.class, name="Build again in different cell"),
        @JsonSubTypes.Type(value = BuildAgainSameCell.class, name="Build again in same cell"),
        @JsonSubTypes.Type(value = BuildDome.class, name="Build Dome"),
        @JsonSubTypes.Type(value = CannotMoveUp.class, name="Cannot move up"),
        @JsonSubTypes.Type(value = BuildBeforeAfterMovement.class, name="BuildBeforeAfterMovement"),
        @JsonSubTypes.Type(value = Down2Levels.class, name = "Down 2 Levels")
})

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

    boolean checkLoseCondition(MoveAction moveAction);

    boolean checkLoseCondition();

    boolean checkLoseCondition(BuildAction buildAction);

    List<Cell> getBuildableCells(Worker worker);

    List<Block> getBlocks(Cell selectedCell);

    List<Cell> getWalkableCells(Worker worker);

    void setGame(Game game);

    boolean canEndTurn();

    boolean canEndTurnAutomatically();

    RuleSetStrategy cloneStrategy(Game game);

}
