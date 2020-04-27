package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "workerId", scope = Worker.class)
public class Worker {

    private Cell position;
    private final Color color;


    @JsonCreator
    private Worker(@JsonProperty("color") Color color) {
        this.color = color;
    }

    public Worker(Cell position, Color color) {
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

    public Worker cloneWorker() {
        return new Worker(this.position.cloneCell(), this.color);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "color=" + color +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker)) return false;
        Worker worker = (Worker) o;
        return position.equals(worker.position) &&
                color == worker.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, color);
    }
}
