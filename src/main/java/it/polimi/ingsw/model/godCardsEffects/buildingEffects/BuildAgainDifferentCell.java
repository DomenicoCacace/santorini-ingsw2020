package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.Turn;
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
        if (this.buildsAvailable>0 && getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell())
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
        if(chosenCell == null)
            return super.getBuildableCells(worker);

        if(this.buildsAvailable>0) {
            List<Cell> secondBuild = new ArrayList<>();
            for (Cell cell : super.getBuildableCells(worker))
                if (cell != chosenCell)
                    secondBuild.add(cell);
            return secondBuild;
        }

        return null;
        }
}
