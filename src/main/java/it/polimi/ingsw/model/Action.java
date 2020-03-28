package it.polimi.ingsw.model;



public class Action {

    private ActionType type;

    public Worker getTargetWorker() {
        return targetWorker;
    }

    private Worker targetWorker;

    private Cell targetCell;
    private Block targetBlock;

    public ActionType getType() {
        return type;
    }

    public Action(Worker targetWorker, Cell targetCell) {
        this.type = ActionType.MOVE;
        this.targetWorker = targetWorker;
        this.targetCell = targetCell;
    }

    public Action(Worker targetWorker, Cell targetCell, Block targetBlock) {
        this.type = ActionType.BUILD;
        this.targetWorker = targetWorker;
        this.targetCell = targetCell;
        this.targetBlock = targetBlock;
    }

    public Cell getStartingCell() {
        return targetWorker.getPosition();
    }
    public Cell getTargetCell() {
        return targetCell;
    }
    public void applier() {
        switch (type){
            case MOVE:
                targetWorker.getPosition().setOccupiedBy(null);
                targetWorker.setPosition(targetCell);
                targetCell.setOccupiedBy(targetWorker);
                break;
            case BUILD:
                targetCell.setBlock(targetBlock);
                break;
            default: break;
        }

    }

}
