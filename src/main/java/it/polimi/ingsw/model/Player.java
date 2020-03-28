package it.polimi.ingsw.model;

import it.polimi.ingsw.model.utilities.Memento;

import java.util.List;

public class Player implements Memento<Player> {
    private final String name;
    private final God god;
    private List<Worker> workers;
    private Worker selectedWorker;
    private Cell selectedCell;
    private Block selectedBlock;
    private Action action;
    private final Color color;
    private Boolean hasMoved;
    private Boolean hasMovedUp;
    private Boolean hasBuilt;
    private Boolean disconnected;
    private Boolean winner;
    private Game game;

    public Player(String name, God god, Color color) {
        this.name = name;
        this.god = god;
        this.color = color;
    }

    public void setGame(Game game) {
        this.game = game;
    }

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

    public void saveWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public List<Worker> getWorkers() {
        return workers;
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

    public Color getColor() {
        return color;
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

    @Override
    public Player saveState() {

        Player savedPlayer = new Player(this.name, this.god, this.color);
        savedPlayer.setHasBuilt(this.hasBuilt);
        savedPlayer.setHasMoved(this.hasMoved);
        savedPlayer.setHasMovedUp(this.hasMovedUp);
        savedPlayer.saveWorkers(this.workers);
        return savedPlayer;

    }

    @Override
    public void restoreState(Player savedState) {
        this.hasBuilt = savedState.getHasBuilt();
        this.hasMoved = savedState.getHasMoved();
        this.hasMovedUp = savedState.getHasMovedUp();
        this.workers = savedState.getWorkers();
    }
}
