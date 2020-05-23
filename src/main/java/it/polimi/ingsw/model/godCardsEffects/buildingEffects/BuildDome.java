package it.polimi.ingsw.model.godCardsEffects.buildingEffects;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

public class BuildDome extends BuildingStrategy {

    public BuildDome() {
        super();
    }

    /**
     * Copy constructor
     *
     * @param buildDome the strategy to clone
     * @param game      the game in which the effect is used
     */
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

    /**
     * Determines if a buildAction is legal and applies it
     * <p>
     * g this ruleSet, a build action is considered valid if the following conditions are all true:
     * <ul>
     *     <li>a worker has already been moved</li>
     *     <li>the worker to perform the action is the same which has been moved</li>
     *     <li>the cell to build on is a buildable cell (see {@linkplain #getBuildableCells(Worker)}) for the worker</li>
     * </ul>
     *
     * @param action the build action to validate
     * @return true if the action has been applied, false otherwise
     */
    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (isInsideBuildableCells(action) && (isCorrectBlock(action) ||
                action.getTargetBlock().getHeight() == 4 && movedWorker == action.getTargetWorker())) {
            buildsAvailable--;
            return true;
        }
        return false;
    }

    /**
     * Creates a clone of this object
     *
     * @param game the current game
     * @return a clone of this object
     */
    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new BuildDome(this, game);
    }

    /**
     * Provides the possible blocks buildable on a given cell
     * <p>
     * g this ruleSet, a worker can build
     * <ul>
     *     <li>a block which level is immediately taller than the cell to build on</li>
     *     <li>a dome</li>
     * </ul>
     * Note that the standard rules about building on domes still apply
     *
     * @param selectedCell the cell to get the buildable blocks for
     * @return a list of blocks that can be built on the given cell
     */
    @Override
    public List<Block> getBlocks(Cell selectedCell) {
        List<Block> buildingBlocks = new ArrayList<>();
        buildingBlocks.add(Block.values()[selectedCell.getBlock().getHeight() + 1]);
        if (buildingBlocks.get(0) != Block.DOME)
            buildingBlocks.add(Block.DOME);
        return buildingBlocks;
    }
}
