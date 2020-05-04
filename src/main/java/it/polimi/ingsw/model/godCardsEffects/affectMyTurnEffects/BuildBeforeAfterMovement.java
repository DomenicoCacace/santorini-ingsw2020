package it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BuildBeforeAfterMovement extends AffectMyTurnStrategy {

    private boolean hasBuiltBefore;
    private Worker builder;
    private int stuckWorkers;




    private BuildBeforeAfterMovement(BuildBeforeAfterMovement buildBeforeAfterMovement, Game game) {
        this.game = game;
        this.movesAvailable = buildBeforeAfterMovement.getMovesAvailable();
        this.movesUpAvailable = buildBeforeAfterMovement.getMovesUpAvailable();
        this.buildsAvailable = buildBeforeAfterMovement.getBuildsAvailable();
        this.hasMovedUp = buildBeforeAfterMovement.hasMovedUp();
        if (buildBeforeAfterMovement.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(buildBeforeAfterMovement.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        this.hasBuiltBefore = buildBeforeAfterMovement.hasBuiltBefore;
        this.builder = game.getGameBoard().getCell(buildBeforeAfterMovement.builder.getPosition()).getOccupiedBy();
        this.stuckWorkers = buildBeforeAfterMovement.stuckWorkers;
    }

    public BuildBeforeAfterMovement() {
        super();
    }

    @Override
    public void initialize() {
        this.movesAvailable = 1;
        this.movesUpAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp = false;
        this.hasBuiltBefore = false;
        this.movedWorker = null;
        this.builder = null;
        this.stuckWorkers = 0;
    }

    @Override
    public void doEffect() {
        initialize();
    }

    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = new ArrayList<>();
        if (buildsAvailable > 0) {
            if (movesAvailable == 0 && !hasBuiltBefore) {
                possibleActions = super.getPossibleActions(worker);
            } else if (movesAvailable == 0 && worker == builder) { //this is useful for the view: highlighting the correct cells
                possibleActions = super.getPossibleActions(worker);
            } else if (movesAvailable == 1 && !hasBuiltBefore) {
                if (getWalkableCells(worker).size() == 0) {
                    if (stuckWorkers==2) {
                        List<Cell> buildableCells = new ArrayList<>();
                        addBuildableCells(worker, buildableCells);
                        if (buildableCells.size() > 0)
                            possibleActions.add(PossibleActions.BUILD);
                    }
                } else if (buildableCellsBeforeMoving(worker).size() > 0)
                    possibleActions.add(PossibleActions.BUILD);
                if (getWalkableCells(worker).size() > 0)
                    possibleActions.add(PossibleActions.MOVE);
                possibleActions.add(PossibleActions.SELECT_OTHER_WORKER);


            } else if (movesAvailable == 1 && worker == builder)
                possibleActions.add(PossibleActions.MOVE);
        }

        return possibleActions;
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        if (!hasBuiltBefore && super.isMoveActionValid(action)) {
            buildsAvailable--;
            startingCell=action.getStartingCell();
            return true;
        }
        return super.isMoveActionValid(action);
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (this.buildsAvailable > 0 && isInsideBuildableCells(action) && isCorrectBlock(action)) {
            if (movedWorker == null) {
                hasBuiltBefore = true;
                builder = action.getTargetWorker();
                buildsAvailable--;
            } else if (movedWorker == action.getTargetWorker())
                buildsAvailable = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean checkLoseCondition() {
        List<Cell> legalCells = new ArrayList<>();
        for (Worker worker : game.getCurrentTurn().getCurrentPlayer().getWorkers()) {
            legalCells = getWalkableCells(worker);
            if(legalCells.size() == 0) {
                stuckWorkers++;
                legalCells = getBuildableCells(worker);
            }
        }
        return legalCells.size()==0;
    }

    @Override
    public boolean checkLoseCondition(BuildAction buildAction) {
        return stuckWorkers==2;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> canGoCells = new ArrayList<>();
        if (hasBuiltBefore) {
            if (worker == builder) {
                for (Cell cell : super.getWalkableCells(worker)) {
                    if (worker.getPosition().heightDifference(cell) <= 0)
                        canGoCells.add(cell);
                }
            }
            return canGoCells;
        }
        return super.getWalkableCells(worker);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        List<Cell> cells = new ArrayList<>();
        if(stuckWorkers<2) {
            if (buildsAvailable > 0) {
                if (movesAvailable == 0 && !hasBuiltBefore) {
                    cells = super.getBuildableCells(worker);
                } else if (movesAvailable == 0 && worker == builder) { //this is useful for the view: highlighting the correct cells
                    cells = super.getBuildableCells(worker);
                } else if (movesAvailable == 1 && !hasBuiltBefore) {
                    cells = buildableCellsBeforeMoving(worker);
                }
            }
        }else addBuildableCells(worker,cells);
        return cells;
    }

    private List<Cell> buildableCellsBeforeMoving(Worker worker) {
        List<Cell> buildableCells = new ArrayList<>();

        int cellsOnMyLevel = 0;
        int heightDifference;
        Cell cellOnMyLevel = null;
        super.addBuildableCells(worker, buildableCells);
        for (Cell cell : buildableCells) {
            heightDifference = worker.getPosition().heightDifference(cell);
            if (heightDifference < 0) {
                return buildableCells;
            } else if (heightDifference == 0) {
                cellsOnMyLevel++;
                if (cellsOnMyLevel > 1)
                    return buildableCells;
                else
                    cellOnMyLevel = cell;
            }
        }
        if (cellsOnMyLevel == 1) {
            buildableCells.remove(cellOnMyLevel);
        } else
            buildableCells = new ArrayList<>();
        return buildableCells;
    }

    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new BuildBeforeAfterMovement(this, game);
    }


}
