package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.Turn;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class BuildAgainSameCell extends BuildingStrategy {

    private Cell chosenCell;

    public BuildAgainSameCell() {
        movesAvailable = 1;
        buildsAvailable = 2;
        hasMovedUp= false;
        this.movedWorker= null;
    }

    @Override
    public void doEffect(Turn turn) {
        movesAvailable = 1;
        buildsAvailable = 2;
        hasMovedUp= false;
        this.movedWorker= null;
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1) &&
                this.buildsAvailable>0 && movedWorker == action.getTargetWorker()){
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
        if(chosenCell == null)
            return super.getBuildableCells(worker);
        List<Cell> secondBuild = new ArrayList<>();
        for(Cell cell : super.getBuildableCells(worker))
            if(cell == chosenCell)
                secondBuild.add(cell);
        return secondBuild;
    }

}
