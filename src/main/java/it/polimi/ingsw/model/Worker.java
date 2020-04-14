package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="workerId", scope = Worker.class)
public class Worker {
    private Cell position;
    private List<Cell> buildableCells;
    private List<Cell> walkableCells;

    public Worker(Cell position) {
        this.position = position;
    }

    @JsonCreator
    private Worker(@JsonProperty("position") Cell position, @JsonProperty("buildableCells") List<Cell> buildableCells, @JsonProperty("walkableCells") List<Cell> walkableCells) {

        this.position = position;
        position.setOccupiedBy(this);
        //this.buildableCells = buildableCells;
        //this.walkableCells = walkableCells;
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
