package it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

public class BuildBeforeAfterMovement extends AffectMyTurnStrategy {

    private boolean hasBuiltBefore;
    private Worker builder;

    private BuildBeforeAfterMovement(BuildBeforeAfterMovement buildBeforeAfterMovement, Game game){
        this.game = game;
        this.movesAvailable = buildBeforeAfterMovement.getMovesAvailable();
        this.movesUpAvailable = buildBeforeAfterMovement.getMovesUpAvailable();
        this.buildsAvailable = buildBeforeAfterMovement.getBuildsAvailable();
        this.hasMovedUp = buildBeforeAfterMovement.hasMovedUp();
        if(buildBeforeAfterMovement.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(buildBeforeAfterMovement.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        this.hasBuiltBefore = buildBeforeAfterMovement.hasBuiltBefore;
        this.builder = game.getGameBoard().getCell(buildBeforeAfterMovement.builder.getPosition()).getOccupiedBy();
    }

    public void initialize() {
        this.movesAvailable = 1;
        this.buildsAvailable = 2;
        this.hasMovedUp= false;
        this.hasBuiltBefore = false;
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
    public boolean isMoveActionValid(MoveAction action) {
        if(!hasBuiltBefore && super.isMoveActionValid(action)){
            buildsAvailable--;
            return true;
        }
        return super.isMoveActionValid(action);
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        if (this.buildsAvailable>0 && isInsideBuildableCells(action) && isCorrectBlock(action)) {
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
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> canGoCells = new ArrayList<>();
        if(hasBuiltBefore){
            if(worker == builder) {
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
        if (buildsAvailable>0) {
            if (movesAvailable == 0 && !hasBuiltBefore) {
                cells = super.getBuildableCells(worker);
            }

            else if (movesAvailable == 0 && worker==builder) { //this is useful for the view: highlighting the correct cells
                cells = super.getBuildableCells(worker);
            }

            else if (movesAvailable == 1 && !hasBuiltBefore) {
               cells = buildableCellsBeforeMoving(worker);
            }
        }
        return cells;
    }

    private List<Cell> buildableCellsBeforeMoving(Worker worker) {
        int cellsOnMyLevel = 0;
        int heightDifference;
        Cell cellOnMyLevel = null;
        List<Cell> buildableCells = new ArrayList<>();
        super.addBuildableCells(worker, buildableCells); //Aggiungo a buildableCells tutte le celle su cui potrei costruire normalmente

        for(Cell cell: buildableCells){
            heightDifference = worker.getPosition().heightDifference(cell);
            if(heightDifference<0){
                return buildableCells; //Se trovo una Cella ad un livello inferiore di quello in cui mi trovo allora non c'è rischio di suicidarmi
            }
            else if (heightDifference == 0){ //Conto quante celle al mio stesso livello ci sono
                cellsOnMyLevel++;
                if(cellsOnMyLevel>1) //Se trovo 2 celle al mio stesso livello allora non c'è rischio di suicidarmi
                    return buildableCells;
                else cellOnMyLevel = cell; //Quando trovo la prima cella al mio stesso livello la salvo in una variabile
            }
        }

        if(cellsOnMyLevel ==1){
            buildableCells.remove(cellOnMyLevel); //Se ho trovato solo una cella al mio stesso livello e non ho trovato celle più in basso allora posso costruire ovunque tranne che nella cella al mio livello
        }
        else buildableCells = new ArrayList<>(); //Se arrivo in questo else è perché sono circondato da celle più in alto, quindi non posso costruire prima di muovermi
        return buildableCells;
    }

    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new BuildBeforeAfterMovement(this, game);
    }


}
