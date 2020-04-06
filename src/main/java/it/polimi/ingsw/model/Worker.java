package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.List;
@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="workerId", scope = Worker.class)
public class Worker {
    private Player owner;
    private Cell position;
    private List<Cell> buildableCells;
    private List<Cell> walkableCells;

    public Worker(Cell position, Player owner) {
        this.position = position;
        this.owner = owner;
    }

    @JsonCreator
    private Worker(@JsonProperty("position") Cell position, @JsonProperty("buildableCells") List<Cell> buildableCells, @JsonProperty("walkableCells") List<Cell> walkableCells) {

        this.position = position;
        position.setOccupiedBy(this);
        //this.buildableCells = buildableCells;
        //this.walkableCells = walkableCells;
    }

    public Player getOwner() {
        return owner;
    }
/*
    @JsonSetter
    public void setOwner(@JsonProperty("owner") Player owner){
        this.owner=owner;
    }
    */


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

    @Override
    public String toString() {
        return "Worker{" +
                "owner=" + owner +
                '}';
    }
}
