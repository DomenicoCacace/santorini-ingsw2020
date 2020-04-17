package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="workerId", scope = Worker.class)
public class Worker {
    private Cell position;
    private final Color color;

    @JsonCreator
    public Worker(@JsonProperty("position") Cell position, @JsonProperty("color") Color color) {
        this.position = position;
        position.setOccupiedBy(this);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Cell getPosition() {
        return position;
    }

    public void setPosition(Cell position) {
        this.position = position;
    }
}
