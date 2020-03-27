package it.polimi.ingsw.model;

import java.util.List;

public class BuildAgainSameCell extends BuildingStrategy{

    @Override
    public boolean isBuildActionValid(Action action) {
        return super.isBuildActionValid(action);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        return super.getBuildableCells(worker);
    }
}
