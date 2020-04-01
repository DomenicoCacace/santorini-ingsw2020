package it.polimi.ingsw.model;

import it.polimi.ingsw.model.action.Action;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final God god;
    private final Color color;
    private final List<Worker> workers;
    private Worker selectedWorker;
    private Cell selectedCell;
    private Block selectedBlock;
    private Action action;
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
        god.getStrategy().setGame(game); //We can either do this here or in the Server class after it called the game constructor.
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
        action.getValidation(game);
    }

    public God getGod() {
        return god;
    }

    public Color getColor() {
        return color;
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
