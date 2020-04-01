package it.polimi.ingsw.model.action;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;

public class MoveAction extends Action {

    public MoveAction(Worker targetWorker, Cell targetCell) {
        super(targetWorker, targetCell);
    }

    public void apply() {
        targetWorker.getPosition().setOccupiedBy(null);
        targetWorker.setPosition(targetCell);
        targetCell.setOccupiedBy(targetWorker);
    }

    public void getValidation(Game game) {
        game.validateMoveAction(this);
    }
}
