package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class BuildAgainSameCell extends BuildingStrategy {

    private Cell chosenCell;

    public void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp= false;
        this.movedWorker= null;
    }

    public BuildAgainSameCell() {
        initialize();
    }

    @Override
    public void doEffect() {
        initialize();
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (this.buildsAvailable>0 && isInsideBuildableCells(action) &&
                isCorrectBlock(action) && movedWorker == action.getTargetWorker()){
            buildsAvailable--;
            chosenCell = action.getTargetCell();
            if(action.getTargetBlock().getHeight() == 3 || action.getTargetBlock().getHeight() == 4)
                buildsAvailable = 0;
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> secondBuild = new ArrayList<>();
        if(this.buildsAvailable >0) {
            if (chosenCell == null)
               secondBuild = super.getBuildableCells(worker);
            else
                secondBuild.add(chosenCell);
        }
        return secondBuild;
    }

    @Override
    public boolean canEndTurn(){
        return (movesAvailable == 0 && buildsAvailable <= 1);
    }
}
