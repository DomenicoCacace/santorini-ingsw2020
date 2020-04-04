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

    public BuildBeforeAfterMovement() {
        this.hasBuilt = false;
        movesAvailable = 1;
        buildsAvailable = 2;
        hasMovedUp= false;
        this.movedWorker= null;
    }

    @Override
    public void doEffect() {
        this.hasBuilt = false;
        movesAvailable = 1;
        buildsAvailable = 2;
        hasMovedUp= false;
        this.movedWorker= null;
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
        if (this.buildsAvailable>0 && getBuildableCells(action.getTargetWorker()).contains(action.getTargetCell())
                && action.getTargetCell().getBlock().getHeight() == (action.getTargetBlock().getHeight() - 1)) {
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
                for (Cell cell : game.getGameBoard().getAdjacentCells(worker.getPosition())) {
                    if (cell.getOccupiedBy() == null && !cell.hasDome())
                        cells.add(cell);
                }
            }
        }
        return cells;
    }

}
