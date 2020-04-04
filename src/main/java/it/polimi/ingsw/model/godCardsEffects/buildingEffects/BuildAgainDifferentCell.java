package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class BuildAgainDifferentCell extends BuildingStrategy {

    private Cell chosenCell;

    public BuildAgainDifferentCell() {
        movesAvailable = 1;
        buildsAvailable = 2;
        hasMovedUp= false;
        movedWorker= null;
        movesUpAvailable = 1;
    }

    @Override
    public void doEffect() {
        movesAvailable = 1;
        buildsAvailable = 2;
        hasMovedUp= false;
        movedWorker= null;
        movesUpAvailable = 1;
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell())
                && action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1)
                && movedWorker == action.getTargetWorker()) {
            buildsAvailable--;
            chosenCell = action.getTargetCell();
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> secondBuild = new ArrayList<>();
        if(this.buildsAvailable> 0) {
            if (chosenCell == null)
                secondBuild = super.getBuildableCells(worker);
            else {
                for (Cell cell : super.getBuildableCells(worker))
                    if (cell != chosenCell)
                        secondBuild.add(cell);
            }
        }
        return secondBuild;
    }
}
