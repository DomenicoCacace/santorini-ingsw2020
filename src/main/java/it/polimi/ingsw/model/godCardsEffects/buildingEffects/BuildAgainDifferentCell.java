package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.LostException;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

public class BuildAgainDifferentCell extends BuildingStrategy {

    private Cell chosenCell;


    private BuildAgainDifferentCell( BuildAgainDifferentCell buildAgainDifferentCell, Game game){
        this.game = game;
        this.movesAvailable = buildAgainDifferentCell.getMovesAvailable();
        this.movesUpAvailable = buildAgainDifferentCell.getMovesUpAvailable();
        this.buildsAvailable = buildAgainDifferentCell.getBuildsAvailable();
        this.hasMovedUp = buildAgainDifferentCell.hasMovedUp();
        if(buildAgainDifferentCell.getMovedWorker() != null)
            this.movedWorker =game.getGameBoard().getCell(buildAgainDifferentCell.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        chosenCell = game.getGameBoard().getCell(buildAgainDifferentCell.chosenCell);
    }

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
    public boolean isBuildActionValid(BuildAction action) throws LostException {
        if (canBuild(action)) {
            buildsAvailable--;
            chosenCell = action.getTargetCell();
            return true;
        }
        return false;
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) throws LostException {
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

    @Override
    public boolean canEndTurn(){
        return (movesAvailable == 0 && buildsAvailable <= 1);
    }

    @Override
    public RuleSetStrategy getClone(Game game) {
        return new BuildAgainDifferentCell(this, game);
    }
}
