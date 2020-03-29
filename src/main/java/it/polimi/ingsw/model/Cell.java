package it.polimi.ingsw.model;

import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Cell {

    /**
     * This class defines the basic unit of the game board.
     * It contains information about the coordinates{@link #coordX #coordY} of the cell on the game board,
     * the worker standing on the cell{@link #occupiedBy} (null if no worker is standing on the cell),
     * the building elements {@link #block} built on the cell, including a boolean flag {@link #hasDome}
     * which indicates the presence of a dome on the building, since in some occasions a dome may be built
     * on non-level 3 building
     */
    private final int coordX, coordY;
    private boolean hasDome;
    private Worker occupiedBy;
    private Block block;

    public Cell(int coordX, int coordY, boolean hasDome, Worker occupiedBy, Block block) {
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

    @Override
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
}
