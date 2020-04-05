package it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.MoveAction;

import java.util.ArrayList;
import java.util.List;

public class BuildBeforeAfterMovement extends AffectMyTurnStrategy {

    private boolean hasBuilt;
    private Worker builder;

    public void initialize() {
        this.movesAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp= false;
        this.hasBuilt = false;
        this.movedWorker= null;
    }

    public BuildBeforeAfterMovement() {
        initialize();
    }

    @Override
    public void doEffect() {
        initialize();
    }

    @Override
    public boolean isMoveActionValid(MoveAction action){
        if(!hasBuilt && super.isMoveActionValid(action)){
            buildsAvailable--;
            return true;
        }
        return super.isMoveActionValid(action);
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (this.buildsAvailable>0 && isInsideBuildableCells(action) && isCorrectBlock(action)) {
            if (movedWorker == null) {
                hasBuilt = true;
                builder = action.getTargetWorker();
                buildsAvailable--;
            } else if (movedWorker == action.getTargetWorker())
                buildsAvailable = 0;
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> canGoCells = new ArrayList<>();
        if(hasBuilt){
            if(worker == builder) {
                for (Cell cell : super.getWalkableCells(worker))
                    if (worker.getPosition().heightDifference(cell) <= 0)
                        canGoCells.add(cell);
            }
            return canGoCells;
        }
        return super.getWalkableCells(worker);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if (buildsAvailable>0) {
            if (movesAvailable == 0 && !hasBuilt) {
                cells= super.getBuildableCells(worker);
            }

            else if (movesAvailable == 0 && worker==builder) {//this is useful for the view: highlighting the correct cells
                cells = super.getBuildableCells(worker);
            }

            else if (movesAvailable == 1 && !hasBuilt) {
                super.addBuildableCells(worker, cells);
            }
        }
        return cells;
    }

}
