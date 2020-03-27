package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class RuleSetBase implements RuleSetStrategy {

    private List<Cell> walkableCells;
    private Game game;

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

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
        System.out.println("buildActionValid in RuleSetBase");
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
        List<Cell> cells = new ArrayList<>();
        for(Cell cell: game.getGameBoard().getAllCells()) {
            if (worker.getPosition().calculateDistance(cell) == 1 && worker.getPosition().heightDifference(cell) <= 1 && cell.getOccupiedBy() == null && !cell.hasDome())
                cells.add(cell);
        }


        return cells;
        }

    @Override
    public List<Cell> getBuildableCells(Worker worker) { // TODO
        //
        return null;
        }
}
