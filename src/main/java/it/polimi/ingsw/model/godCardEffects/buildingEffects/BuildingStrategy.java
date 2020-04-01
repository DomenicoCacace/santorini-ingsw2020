package it.polimi.ingsw.model.godCardEffects.buildingEffects;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;

import java.util.List;

public class BuildingStrategy extends RuleSetBase {


    @Override
    public boolean isBuildActionValid(BuildAction action) {
        return super.isBuildActionValid(action);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        return super.getBuildableCells(worker);
    }
}
