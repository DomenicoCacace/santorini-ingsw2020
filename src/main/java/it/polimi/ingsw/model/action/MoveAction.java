package it.polimi.ingsw.model.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Worker;

import java.io.IOException;

public class MoveAction extends Action {

    public MoveAction(@JsonProperty("targetWorker")Worker targetWorker, @JsonProperty("targetCell") Cell targetCell) {
        super(targetWorker, targetCell);
    }

    public void apply() {
        targetWorker.getPosition().setOccupiedBy(null);
        targetWorker.setPosition(targetCell);
        targetCell.setOccupiedBy(targetWorker);
    }

    public void getValidation(Game game) throws IOException, IllegalActionException {
        game.validateMoveAction(this);
    }
}
