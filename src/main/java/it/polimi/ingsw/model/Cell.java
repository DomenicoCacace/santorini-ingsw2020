package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;

import static java.lang.Math.*;


@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property= "@id", scope = Cell.class)
@JsonPropertyOrder({ "idCell", "coordX", "coordY", "hasDome", "block" })
public class Cell implements Serializable {
    private final int coordX;
    private final int  coordY;
    private boolean hasDome;
    private Block block;
    @JsonBackReference(" cell - worker")
    private Worker occupiedBy;

    @JsonCreator
    public Cell(@JsonProperty("coordX")int coordX, @JsonProperty("coordY") int coordY, @JsonProperty("hasDome") boolean hasDome, @JsonProperty("occupiedBy") Worker occupiedBy, @JsonProperty("block") Block block) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.hasDome = hasDome;
        this.occupiedBy = occupiedBy;
        this.block = block;
    }

    public Cell(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.hasDome = false;
        this.occupiedBy = null;
        this.block = Block.LEVEL0;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    @JsonGetter(value = "hasDome")
    public boolean hasDome() {
        return hasDome;
    }

    public void setHasDome(boolean hasDome) {
        this.hasDome = hasDome;
    }

    public Worker getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(Worker occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
        if (block.equals(Block.DOME)) {
            this.setHasDome(true);
        }
    }

    @Deprecated
    public int calculateDistance(Cell cell) {
        return max(abs(this.coordX - cell.coordX), abs(this.coordY - cell.coordY));
    }

    public int heightDifference(Cell cell) {
        return cell.block.getHeight() - this.block.getHeight();

    }


    @Override
    public String toString() {
        if(occupiedBy!=null) {
            return "Cell{" +
                    "coordX=" + coordX +
                    ", coordY=" + coordY +
                    ", hasDome=" + hasDome +
                    ", is not empty" +
                    // ", occupiedBy=" + occupiedBy +
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

   /* @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Cell))
            return false;
        Cell cell = (Cell) obj;
        return (cell.coordX == coordX) && (cell.coordY == coordY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordX, coordY);
    }

    */


}
