package it.polimi.ingsw.model;

import java.util.List;

public class Worker {
    private final Player owner;
    private Cell position;
    private List<Cell> buildableCells;
    private List<Cell> walkableCells;

    public Worker(Cell position, Player owner) {
        this.position = position;
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public Cell getPosition() {
        return position;
    }

    public void setPosition(Cell position) {
        this.position = position;
    }

    public List<Cell> getBuildableCells() {
        return buildableCells;
    }

    public void setBuildableCells(List<Cell> buildableCells) {
        this.buildableCells = buildableCells;
    }

    public List<Cell> getWalkableCells() {
        return walkableCells;
    }

    public void setWalkableCells(List<Cell> walkableCells) {
        this.walkableCells = walkableCells;
    }


}
