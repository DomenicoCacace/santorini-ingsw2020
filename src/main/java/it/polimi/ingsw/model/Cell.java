package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.Objects;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property= "@id", scope = Cell.class)
@JsonPropertyOrder({ "idCell", "coordX", "coordY", "hasDome", "block" })
public class Cell implements Serializable {
    private final int coordX;
    private final int  coordY;
    private boolean hasDome;
    private Block block;
    @JsonBackReference(" cell - worker")
    private Worker occupiedBy;

    public Cell(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.hasDome = false;
        this.occupiedBy = null;
        this.block = Block.LEVEL0;
    }

    @JsonCreator
    public Cell(@JsonProperty("coordX")int coordX, @JsonProperty("coordY") int coordY, @JsonProperty("hasDome") boolean hasDome, @JsonProperty("occupiedBy") Worker occupiedBy, @JsonProperty("block") Block block) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.hasDome = hasDome;
        this.occupiedBy = occupiedBy;
        this.block = block;
    }

    //Copy constructor
    private Cell(Cell cell){
        this.coordX = cell.coordX;
        this.coordY = cell.coordY;
        this.hasDome = cell.hasDome;
        this.block = cell.block;
        if(cell.occupiedBy != null)
            this.occupiedBy = cell.occupiedBy;
        else this.occupiedBy = null;
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

    public int heightDifference(Cell cell) {
        return cell.block.getHeight() - this.block.getHeight();

    }

    public Cell cloneCell(){
        return new Cell(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return  coordX == cell.coordX &&
                coordY == cell.coordY &&
                hasDome == cell.hasDome &&
                block == cell.block;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordX, coordY, hasDome, block);
    }
}
