package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;

public class BuildAgainDifferentCell extends BuildingStrategy {

    private Cell chosenCell;

    public void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp= false;
        this.movedWorker= null;
    }

    public BuildAgainDifferentCell() {
        initialize();
    }

    @Override
    public void doEffect() {
        initialize();
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (canBuild(action)) {
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
