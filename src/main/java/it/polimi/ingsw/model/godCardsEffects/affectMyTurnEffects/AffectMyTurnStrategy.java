package it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Turn;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;

import java.util.List;

public class AffectMyTurnStrategy extends RuleSetBase {

    @Override
    public void doEffect(Turn turn) {
        super.doEffect(turn);
    }

    @Override
    public boolean isMoveActionValid(MoveAction action) {
        return super.isMoveActionValid(action);
    }

    @Override
    public boolean isBuildActionValid(BuildAction action) {
        return super.isBuildActionValid(action);
    }

    @Override
    public List<Cell> getWalkableCells(Worker worker) {
        return super.getWalkableCells(worker);
    }

    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        return super.getBuildableCells(worker);
    }
}
