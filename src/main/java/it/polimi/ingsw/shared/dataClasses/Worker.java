package it.polimi.ingsw.shared.dataClasses;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

/**
 * Worker representation
 * <p>
 * The Workers are the pawns of this game. By default, each player gets at the beginning of the game two workers,
 * which are placed on an empty {@link Cell} of the board, at the beginning of the game.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "workerId", scope = Worker.class)
public class Worker {

    private final Color Color;
    private Cell position;

    /**
     * Jackson constructor
     *
     * @param Color the worker's color
     */
    @JsonCreator
    private Worker(@JsonProperty("color") Color Color) {
        this.Color = Color;
    }

    /**
     * Class Constructor
     * <p>
     * The constructor sets the two class attributes and assigns the <i>occupiedBy</i> field in the given cell to
     * the newly created worker.
     * <br>
     * This constructor is also used to restore a previous state in case of a server failure.
     *
     * @param position the cell to place the new worker on
     * @param Color    the color of the worker
     */
    public Worker(Cell position, Color Color) {
        this.position = position;
        position.setOccupiedBy(this);
        this.Color = Color;
    }

    /**
     * <i>color</i> getter
     *
     * @return the color of the worker
     */
    public Color getColor() {
        return Color;
    }

    /**
     * <i>position</i> getter
     *
     * @return the Cell containing the Worker
     */
    public Cell getPosition() {
        return position;
    }


    /**
     * <i>position</i> setter
     * <p>
     * Used to move a Worker around on the map; this method does not refresh the <i>occupiedBy</i> attribute in
     * the corresponding {@link Cell}.
     *
     * @param position the Cell to move the Worker to
     */
    @JsonAnySetter
    public void setPosition(Cell position) {
        this.position = position;
    }

    /**
     * Creates a clone of this worker
     *
     * @return a clone of the worker
     */
    public Worker cloneWorker() {
        return new Worker(this.position.cloneCell(), this.Color);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "color=" + Color +
                '}';
    }

    /**
     * Compares the argument to the receiver, and answers true if their coordinates and color are equals
     *
     * @param o the object to be compared with this
     * @return true if the object is the same as the cell, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker)) return false;
        Worker worker = (Worker) o;
        return position.equals(worker.position) &&
                Color == worker.Color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, Color);
    }
}
