package it.polimi.ingsw.model.action;


import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.LostException;
import it.polimi.ingsw.model.Worker;

import java.io.IOException;

public abstract class Action {

    protected final Cell targetCell;
    protected final Worker targetWorker;

    protected Action(Worker targetWorker, Cell targetCell) {
        this.targetCell = targetCell;
        this.targetWorker = targetWorker;
    }

    public Worker getTargetWorker() {
        return targetWorker;
    }

    public Cell getStartingCell() {
        return this.targetWorker.getPosition();
    }

    public abstract void getValidation(Game game) throws IOException, LostException;

    public Cell getTargetCell() {
        return targetCell;
    }

    public abstract void apply();

}
