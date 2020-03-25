package it.polimi.ingsw.model;



public class Action {
    private ActionType type;
    private Worker targetWorker;
    private Cell targetCell;
    private Block targetBlock;

    public Action(ActionType type, Worker targetWorker, Cell targetCell) {
        this.type = ActionType.MOVE;
        this.targetWorker = targetWorker;
        this.targetCell = targetCell;
    }

    public Action(ActionType type, Worker targetWorker, Cell targetCell, Block targetBlock) {
        this.type = ActionType.BUILD;
        this.targetWorker = targetWorker;
        this.targetCell = targetCell;
        this.targetBlock = targetBlock;
    }

    public void applier(){
        switch (type){
            case MOVE:
                this.targetWorker.getPosition().setOccupiedBy(null);
                this.targetWorker.setPosition(targetCell);
                targetCell.setOccupiedBy(targetWorker);
                break;
            case BUILD:
                targetCell.setBlock(targetBlock);
                break;
            default: break;
        }

    }

}
