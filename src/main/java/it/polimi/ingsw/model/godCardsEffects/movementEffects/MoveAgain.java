package it.polimi.ingsw.model.godCardsEffects.movementEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.ArrayList;
import java.util.List;

public class MoveAgain extends MovementStrategy {

    private Cell startingCell;


    public MoveAgain() {
        super();
    }

    private MoveAgain(MoveAgain moveAgain, Game game) {
        this.game = game;
        this.movesAvailable = moveAgain.getMovesAvailable();
        this.movesUpAvailable = moveAgain.getMovesUpAvailable();
        this.buildsAvailable = moveAgain.getBuildsAvailable();
        this.hasMovedUp = moveAgain.hasMovedUp();
        if (moveAgain.getMovedWorker() != null)
            this.movedWorker = game.getGameBoard().getCell(moveAgain.getMovedWorker().getPosition()).getOccupiedBy();
        else this.movedWorker = null;
        this.startingCell = game.getGameBoard().getCell(moveAgain.startingCell);
    }

    @Override
    public void initialize() {
        this.movesAvailable = 2;
        this.movesUpAvailable = 2;
        this.buildsAvailable = 1;
        this.hasMovedUp = false;
        this.movedWorker = null;
        this.startingCell = null;
    }

    @Override
    public void doEffect() {
        initialize();
    }

    @Override
    public List<PossibleActions> getPossibleActions(Worker worker) {
        List<PossibleActions> possibleActions = new ArrayList<>();
        if (movesAvailable == 1) {
            possibleActions.add(PossibleActions.BUILD);
            if (getWalkableCells(worker).size() > 0)
                possibleActions.add(PossibleActions.MOVE);
        } else possibleActions = super.getPossibleActions(worker);
        return possibleActions;
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        int x, y;
        if (movedWorker == null && super.isMoveActionValid(action)) {
            x = action.getTargetWorker().getPosition().getCoordX();
            y = action.getTargetWorker().getPosition().getCoordY();
            startingCell = game.getGameBoard().getCell(x, y);
            return true;
        } else if (movedWorker == action.getTargetWorker())
            return super.isMoveActionValid(action);
        return false;
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        List<Cell> adjacentCells = super.getWalkableCells(worker);
        if (movedWorker != null)
            adjacentCells.remove(startingCell);
        return adjacentCells;
    }

    @Override
    public RuleSetStrategy cloneStrategy(Game game) {
        return new MoveAgain(this, game);
    }
}
