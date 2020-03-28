package it.polimi.ingsw.model;

public enum Block {
    LEVEL0(0), LEVEL1(1), LEVEL2(2), LEVEL3(3), DOME(4);

    private final int height;
    private Block (int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }



}
