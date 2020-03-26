package it.polimi.ingsw.model;

import java.util.List;

public class Player {
    private String name;
    private God god;
    private List<Worker> workers;
    private Worker selectedWorker;
    private Cell selectedCell;
    private Block selectedBlock;
    private Action action;
    private Color color;
    private Boolean hasMoved;
    private Boolean hasMovedUp;
    private Boolean hasBuilt;
    private Boolean disconnected;
    private Boolean winner;

    public void setWorkers(List<Cell> cells) {
        for(Cell cell: cells) {

            if(cell.getOccupiedBy() == null) {
                Worker worker = new Worker(cell, this);
                workers.add(worker);
            }
            else {
                //TODO: manage already occupied cells
            }
        }
    }

    private Game game;

    public void setGame(Game game) {
        this.game = game;
    }

    public Player(String name, God god, Color color) {
        this.name = name;
        this.god = god;
        this.color = color;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void useAction(){
        game.validateAction(action);
    }

    public God getGod() {
        return god;
    }

    public void setGod(God god) {
        this.god = god;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(Boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean getHasMovedUp() {
        return hasMovedUp;
    }

    public void setHasMovedUp(Boolean hasMovedUp) {
        this.hasMovedUp = hasMovedUp;
    }

    public boolean getHasBuilt() {
        return hasBuilt;
    }

    public void setHasBuilt(Boolean hasBuilt) {
        this.hasBuilt = hasBuilt;
    }

    public void setSelectedWorker(Worker selectedWorker) {
        this.selectedWorker = selectedWorker;
        selectedWorker.setWalkableCells(game.getWalkableCells(selectedWorker));
    }

    public void setSelectedCell(Cell selectedCell) {
        this.selectedCell = selectedCell;
    }

    public void setSelectedBlock(Block selectedBlock) {
        this.selectedBlock = selectedBlock;
    }

    public void setDisconnected(Boolean disconnected) {
        this.disconnected = disconnected;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    public boolean isWinner() {
        return winner;
    }

}
