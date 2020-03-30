package it.polimi.ingsw.model;


public class Action {

    private final ActionType type;
    private final Cell targetCell;

    public Block getTargetBlock() {
        return targetBlock;
    }

    private final Block targetBlock;
    private final Worker targetWorker;


    public Action(Worker targetWorker, Cell targetCell) {
        this.type = ActionType.MOVE;
        this.targetWorker = targetWorker;
        this.targetCell = targetCell;
        this.targetBlock = null;
    }

    public Action(Worker targetWorker, Cell targetCell, Block targetBlock) {
        this.type = ActionType.BUILD;
        this.targetWorker = targetWorker;
        this.targetCell = targetCell;
        this.targetBlock = targetBlock;
    }

    public Worker getTargetWorker() {
        return targetWorker;
    }

    public ActionType getType() {
        return type;
    }

    public Cell getStartingCell() {
        return this.targetWorker.getPosition();

    }

    public Cell getTargetCell() {
        return targetCell;
    }

    public void applier() {
        switch (type) {
            case MOVE:
                targetWorker.getPosition().setOccupiedBy(null);
                targetWorker.setPosition(targetCell);
                targetCell.setOccupiedBy(targetWorker);
                break;
            case BUILD:
                assert targetBlock != null;
                targetCell.setBlock(targetBlock);
                break;
            default:
                break;
        }

    }

}
