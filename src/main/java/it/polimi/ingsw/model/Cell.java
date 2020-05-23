package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * The base unit of the game board
 * <p>
 * class contains all information about a cell inside a gameBoard, such as:
 * <ul>
 *     <li>Position, via {@link #coordX} and {@link #coordY} (see {@link GameBoard};</li>
 *     <li>Building state, via {@link #block} and {@link #hasDome} (see {@link Block});</li>
 *     <li>Occupant, via {@link #occupiedBy} (see {@link Worker});</li>
 * </ul>
 * This class uses {@linkplain JacksonAnnotation}s to export the internal status to an external JSON file, in order to
 * save the current state and recover it in case of a server failure.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = Cell.class)
@JsonPropertyOrder({"idCell", "coordX", "coordY", "hasDome", "block"})
public class Cell implements Serializable {


    private final int coordX;
    private final int coordY;
    private boolean hasDome;
    private Block block;
    private Worker occupiedBy;


    /**
     * Default constructor
     * <p>
     * Creates a new Cell, assigning its coordinates. At the time of their creation, the Cells are empty; this
     * means that there are no buildings nor workers.
     *
     * @param coordX the X coordinate of the cell to create
     * @param coordY the Y coordinate of the cell to create
     */
    public Cell(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.hasDome = false;
        this.occupiedBy = null;
        this.block = Block.LEVEL0;
    }

    /**
     * Jackson Constructor
     * <p>
     * Loads a previously saved Cell state; the Cell is "reassembled" in this constructor (after reading the saved
     * state from file) instead of being built during the file parsing to avoid reference problems.
     *
     * @param coordX     the X coordinate of the cell to restore
     * @param coordY     the Y coordinate of the cell to restore
     * @param hasDome    the hasDome flag of the cell to restore
     * @param occupiedBy the Worker occupying the cell
     * @param block      the building block on the cell
     */
    @JsonCreator
    public Cell(@JsonProperty("coordX") int coordX, @JsonProperty("coordY") int coordY,
                @JsonProperty("hasDome") boolean hasDome, @JsonProperty("occupiedBy") Worker occupiedBy,
                @JsonProperty("block") Block block) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.hasDome = hasDome;
        this.occupiedBy = occupiedBy;
        this.block = block;
    }

    /**
     * Copy constructor
     * <p>
     * Creates a clone of the given cell; used in the {@link #cloneCell()} method, passing <i>this</i> as the
     * parameter.
     *
     * @param cell the cell to clone
     */
    private Cell(Cell cell) {
        this.coordX = cell.coordX;
        this.coordY = cell.coordY;
        this.hasDome = cell.hasDome;
        this.block = cell.block;
        if (cell.occupiedBy != null)
            this.occupiedBy = new Worker(this, cell.occupiedBy.getColor());
        else this.occupiedBy = null;
    }

    /**
     * <i>coordX</i> getter
     *
     * @return the X coordinate of the cell
     */
    public int getCoordX() {
        return coordX;
    }

    /**
     * <i>coordY</i> getter
     *
     * @return the Y coordinate of the cell
     */
    public int getCoordY() {
        return coordY;
    }

    /**
     * <i>hasDome</i> getter
     *
     * @return true if there is a dome on the cell, false otherwise
     */
    @JsonGetter(value = "hasDome")
    public boolean hasDome() {
        return hasDome;
    }

    /**
     * <i>hasDome</i> setter
     * <p>
     * Following the classic game flow, this method is used only to set the hasDome attribute to true upon a dome
     * construction; since there might be additional gods that allows a dome to be removed, we decided to leave it
     * as a setter. At the current stage of development, this method is always called passing <i>true</i> as a
     * parameter.
     *
     * @param hasDome boolean flag to notify the dome presence
     */
    public void setHasDome(boolean hasDome) {
        this.hasDome = hasDome;
    }

    /**
     * <i>occupiedBy</i> getter
     *
     * @return the {@link Worker} on the cell if present, <i>null</i> otherwise
     */
    public Worker getOccupiedBy() {
        return occupiedBy;
    }

    /**
     * <i>occupiedBy</i> setter
     *
     * @param occupiedBy the {@linkplain Worker} to be placed on the cell
     */
    public void setOccupiedBy(Worker occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    /**
     * <i>block</i> getter
     *
     * @return the last {@linkplain Block} built on the cell
     */
    public Block getBlock() {
        return block;
    }

    /**
     * <i>block</i> setter
     *
     * @param block the {@linkplain Block} to build on the cell
     */
    public void setBlock(Block block) {
        this.block = block;
        if (block.equals(Block.DOME)) {
            this.setHasDome(true);
        }
    }

    /**
     * Provides the height difference between two cells
     * <p>
     * Returns the height difference as <i>(parameterCell.height - thisCell.height).</i>
     * <br>
     * The height difference between two cells is defined as the difference between the {@linkplain Block}s
     * built on the two cells.
     *
     * @param cell the {@linkplain Cell} to compare
     * @return the height difference
     */
    public int heightDifference(Cell cell) {
        return cell.block.getHeight() - this.block.getHeight();

    }

    /**
     * Creates a clone of the cell
     * <p>
     * Using the private constructor to create a new Cell instance having the same attributes; used to clone and
     * send the gameBoard over the network.
     *
     * @return a clone of the cell
     */
    public Cell cloneCell() {
        return new Cell(this);
    }


    @Override
    public String toString() {
        if (occupiedBy != null) {
            return "Cell{" +
                    "coordX=" + coordX +
                    ", coordY=" + coordY +
                    ", hasDome=" + hasDome +
                    ", is not empty" +
                    ", occupiedBy=" + occupiedBy +
                    ", block=" + block +
                    "}\n";
        }
        return "Cell{" +
                "coordX=" + coordX +
                ", coordY=" + coordY +
                ", hasDome=" + hasDome +
                ", block=" + block +
                "}\n";

    }

    /**
     * Compares the argument to the receiver, and answers true if their coordinates and blocks built (including dome)
     * are equals
     *
     * @param o the object to be compared  with this
     * @return true if the object is the same as the cell, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return coordX == cell.coordX &&
                coordY == cell.coordY &&
                hasDome == cell.hasDome &&
                block == cell.block;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordX, coordY, hasDome, block);
    }
}
