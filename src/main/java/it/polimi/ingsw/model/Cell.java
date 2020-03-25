package it.polimi.ingsw.model;

import java.util.Objects;

import static java.lang.Math.*;

public class Cell {
    private final int coordX, coordY;
    private boolean hasDome;
    private Worker occupiedBy;
    private Block block;

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



    public boolean HasDome() {
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
        if (block.equals(Block.DOME)){
            this.setHasDome(true);
        }
    }

    public int calculateDistance(Cell cell){
        return max(abs(this.coordX - cell.coordX), abs(this.coordY - cell.coordY));
    }

    public int heightDifference(Cell cell){
        return cell.block.getHeight() - this.block.getHeight();

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Cell))
            return false;
        Cell cell = (Cell)obj;
        return (cell.coordX == coordX) && (cell.coordY == coordY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordX, coordY);
    }
}
