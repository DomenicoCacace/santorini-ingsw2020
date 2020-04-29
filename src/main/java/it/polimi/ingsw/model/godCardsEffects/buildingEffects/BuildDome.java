package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

public class BuildDome extends BuildingStrategy {

    public BuildDome() {
        super();
    }

    private BuildDome(BuildDome buildDome, Game game) {
        this.game = game;
        this.movesAvailable = buildDome.getMovesAvailable();
        this.movesUpAvailable = buildDome.getMovesUpAvailable();
        this.buildsAvailable = buildDome.getBuildsAvailable();
        this.hasMovedUp = buildDome.hasMovedUp();
        if (buildDome.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(buildDome.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (isInsideBuildableCells(action) && (isCorrectBlock(action) ||
                action.getTargetBlock().getHeight() == 4 && movedWorker == action.getTargetWorker())) {
            buildsAvailable--;
            return true;
        }
        return false;
    }

    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new BuildDome(this, game);
    }

    @Override
    public List<Block> getBlocks(Cell selectedCell) {
        List<Block> buildingBlocks = new ArrayList<>();
        buildingBlocks.add(Block.values()[selectedCell.getBlock().getHeight() + 1]);
        if (buildingBlocks.get(0) != Block.DOME)
            buildingBlocks.add(Block.DOME);
        return buildingBlocks;
    }
}
