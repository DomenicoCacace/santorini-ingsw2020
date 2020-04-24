package it.polimi.ingsw.model.rules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
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

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleSetBase.class, name = "Basic rules"),
        @JsonSubTypes.Type(value = Push.class, name = "Push opponent"),
        @JsonSubTypes.Type(value = Swap.class, name = "Swap with opponent"),
        @JsonSubTypes.Type(value = MoveAgain.class, name = "Move again"),
        @JsonSubTypes.Type(value = BuildAgainDifferentCell.class, name = "Build again but in different cells"),
        @JsonSubTypes.Type(value = BuildAgainSameCell.class, name = "Build again in the same cell"),
        @JsonSubTypes.Type(value = BuildDome.class, name = "Build dome"),
        @JsonSubTypes.Type(value = CannotMoveUp.class, name = "Stop the opponents from moving up"),
        @JsonSubTypes.Type(value = BuildBeforeAfterMovement.class, name = "Build before and after movement"),

})
public class RuleSetBase implements RuleSetStrategy {

    protected Game game;
    protected int movesAvailable;
    protected int movesUpAvailable;
    protected int buildsAvailable;
    protected boolean hasMovedUp;
    protected Worker movedWorker;

    public RuleSetBase() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 1;
        this.hasMovedUp = false;
        this.movedWorker = null;
    }

    public RuleSetBase(RuleSetStrategy ruleSetBase, Game game) {
        this.game = game;
        this.movesAvailable = ruleSetBase.getMovesAvailable();
        this.movesUpAvailable = ruleSetBase.getMovesUpAvailable();
        this.buildsAvailable = ruleSetBase.getBuildsAvailable();
        this.hasMovedUp = ruleSetBase.hasMovedUp();
        if (ruleSetBase.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(ruleSetBase.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    @Override
    public int getMovesAvailable() {
        return movesAvailable;
    }

    @Override
    public int getMovesUpAvailable() {
        return movesUpAvailable;
    }

    @Override
    public boolean hasMovedUp() {
        return hasMovedUp;
    }

    @Override
    public int getBuildsAvailable() {
        return buildsAvailable;
    }

    @Override
    public Worker getMovedWorker() {
        return movedWorker;
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void setMovesUpAvailable(int num) {
        this.movesUpAvailable = num;
    }

    @Override
    public void doEffect() {
        movesAvailable = 1;
        movesUpAvailable = 1;
        buildsAvailable = 1;
        hasMovedUp = false;
        movedWorker = null;
    }


    protected boolean isInsideWalkableCells(MoveAction action) {
        return getWalkableCells(action.getTargetWorker()).contains(action.getTargetCell());

    }

    @Override
    public List<PossibleActions> getPossibleActions(Worker worker){
        List<PossibleActions> possibleActions = new ArrayList<>();
        if(getWalkableCells(worker).size() > 0 ) {
            possibleActions.add(PossibleActions.MOVE);
            possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);
        }
        else if (getBuildableCells(worker).size() > 0){
            possibleActions.add(PossibleActions.BUILD);
        }
        else
            possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);
        return possibleActions;


    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (isInsideWalkableCells(action)) {
            movesAvailable--;

            if (movesUpAvailable > 0)
                movesUpAvailable--;

            if (action.getTargetWorker().getPosition().heightDifference(action.getTargetCell()) == 1)
                hasMovedUp = true;
            movedWorker = action.getTargetWorker();
            return true;
        }
        return false;
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (canBuild(action)) {
            buildsAvailable--;
            movesAvailable = 0;
            movesUpAvailable = 0;
            return true;
        }
        return false;
    }


    protected boolean isInsideBuildableCells(BuildAction action) {
        return getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell());
    }

    protected boolean canBuild(BuildAction action) {
        return isInsideBuildableCells(action) && isCorrectBlock(action) &&
                movedWorker == action.getTargetWorker();
    }


    protected boolean isCorrectBlock(BuildAction action) {
        return action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1);
    }

    @Override
    public boolean checkWinCondition(MoveAction action) {
        //removed action.getTargetCell().hasDome()
        return action.getTargetCell().getBlock().getHeight() == 3 &&
                action.getStartingCell().heightDifference(action.getTargetCell()) == 1;
    }

    @Override
    public boolean checkLoseCondition() {
        /* calling this function at the beginning of the turn
         * to check if the player can make a move
         */
        int legalCells = 0;
        for (Worker worker : game.getCurrentTurn().getCurrentPlayer().getWorkers())
            legalCells += getWalkableCells(worker).size();
        return legalCells == 0;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (movesAvailable > 0) {
            addWalkableCells(worker, cells);
        }
        return cells;
    }


    protected void addWalkableCells(Worker worker, List<Cell> cells) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if ((!cell.hasDome() && cell.getOccupiedBy() == null) && isCorrectDistance(worker, cell))
                cells.add(cell);
        }
    }


    protected boolean isCorrectDistance(Worker worker, Cell cell) {
        return (worker.getPosition().heightDifference(cell) <= 0) || (worker.getPosition().heightDifference(cell) == 1 && movesUpAvailable > 0);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (buildsAvailable > 0) {
            if (worker == movedWorker) {
                addBuildableCells(worker, cells);
            }
        }
        return cells;
    }


    protected void addBuildableCells(Worker worker, List<Cell> cells) {
        for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
            if (cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }
    }

    @Override
    public boolean canEndTurn() {
        return canEndTurnAutomatically();
    }

    @Override
    public boolean canEndTurnAutomatically() {
        return (movesAvailable == 0 && buildsAvailable == 0);
    }

    public RuleSetStrategy cloneStrategy(Game game) {
        return new RuleSetBase(this, game);
    }


}
