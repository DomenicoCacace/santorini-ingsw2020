package it.polimi.ingsw.model;

import java.util.List;

public class RuleSetBase implements RuleSetStrategy {

    @Override
    public void doEffect(Turn turn) { }

    @Override
    public int propagateEffect() { // TODO
        //
        return 0;
    }

    @Override
    public boolean isMoveActionValid(Action action) { // TODO
        //
        System.out.println("moveActionValid in RuleSetBase");
        return false;
    }

    @Override
    public boolean isBuildActionValid(Action action) { // TODO
        //
        return false;
        }

    @Override
    public boolean checkWinCondition(Action action) { // TODO
        //
        return false;
        }

    @Override
    public List<Cell> getWalkableCells(Worker worker) { // TODO
        //
        return null;
        }

    @Override
    public List<Cell> getBuildableCells(Worker worker) { // TODO
        //
        return null;
        }
}
