package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.LostException;
import it.polimi.ingsw.model.action.BuildAction;

public class BuildDome extends BuildingStrategy {

    @Override
    public boolean isBuildActionValid(BuildAction action) throws LostException {
        if (isInsideBuildableCells(action) && (isCorrectBlock(action) ||
                action.getTargetBlock().getHeight()==4 && movedWorker == action.getTargetWorker())){
            buildsAvailable--;
            return true;
        }
        return false;
    }

}
