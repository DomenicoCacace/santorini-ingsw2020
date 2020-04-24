package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

public class BuildAgainSameCell extends BuildingStrategy {

    private Cell chosenCell;

    private BuildAgainSameCell(BuildAgainSameCell buildAgainSameCell, Game game) {
        this.game = game;
        this.movesAvailable = buildAgainSameCell.getMovesAvailable();
        this.movesUpAvailable = buildAgainSameCell.getMovesUpAvailable();
        this.buildsAvailable = buildAgainSameCell.getBuildsAvailable();
        this.hasMovedUp = buildAgainSameCell.hasMovedUp();
        if (buildAgainSameCell.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(buildAgainSameCell.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        chosenCell = game.getGameBoard().getCell(buildAgainSameCell.chosenCell);

    }


    public void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp = false;
        this.movedWorker = null;
    }

    public BuildAgainSameCell() {
        initialize();
    }

    @Override
    public void doEffect() {
        initialize();
    }

    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = super.getPossibleActions(worker);
        if(buildsAvailable==1)
            possibleActions.add(PossibleActions.PASSTURN);
        return possibleActions;
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (this.buildsAvailable > 0 && isInsideBuildableCells(action) &&
                isCorrectBlock(action) && movedWorker == action.getTargetWorker()) {
            buildsAvailable--;
            chosenCell = action.getTargetCell();
            if (action.getTargetBlock().getHeight() == 3 || action.getTargetBlock().getHeight() == 4)
                buildsAvailable = 0;
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> secondBuild = new ArrayList<>();
        if (this.buildsAvailable > 0) {
            if (chosenCell == null)
                secondBuild = super.getBuildableCells(worker);
            else
                secondBuild.add(chosenCell);
        }
        return secondBuild;
    }

    @Override
    public boolean canEndTurn() {
        return (movesAvailable == 0 && buildsAvailable <= 1);
    }

    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new BuildAgainSameCell(this, game);
    }


}
