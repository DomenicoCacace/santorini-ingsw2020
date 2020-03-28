package it.polimi.ingsw.model.godCardEffects.buildingEffects;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.List;

public class BuildAgainSameCell extends BuildingStrategy {

    @Override
    public boolean isBuildActionValid(Action action) {
        return super.isBuildActionValid(action);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        return super.getBuildableCells(worker);
    }
}
