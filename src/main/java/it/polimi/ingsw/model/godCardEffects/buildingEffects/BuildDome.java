package it.polimi.ingsw.model.godCardEffects.buildingEffects;

import it.polimi.ingsw.model.action.BuildAction;

public class BuildDome extends BuildingStrategy {

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell()) &&
                (action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1) || action.getTargetBlock().getHeight()==4) &&
                buildsAvailable>0 && movedWorker == action.getTargetWorker()){
            buildsAvailable--;
            return true;
        }
        return false;
    }

}
