package it.polimi.ingsw.model;

import it.polimi.ingsw.model.utilities.Memento;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final God god;
    private final Color color;
    private List<Worker> workers;
    private Worker selectedWorker;
    private Cell selectedCell;
    private Block selectedBlock;
    private Action action;
    private boolean hasMoved;
    private boolean hasMovedUp;
    private boolean hasBuilt;
    private boolean disconnected;
    private boolean winner;
    private Game game;

    public Player(String name, God god, Color color) {
        this.name = name;
        this.god = god;
        this.color = color;
        this.workers = new ArrayList<>();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void addWorker(Cell cell) {
        if (cell.getOccupiedBy() == null) {
            Worker worker = new Worker(cell, this);
            workers.add(worker);
            cell.setOccupiedBy(worker);
        } else {
            //TODO: manage already occupied cell
        }
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void useAction() {
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

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(Boolean disconnected) {
        this.disconnected = disconnected;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

}
