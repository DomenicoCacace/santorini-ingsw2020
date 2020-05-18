package it.polimi.ingsw.model;

/**
 * The buildings' level
 * <ul>
 *     <li>Level 0: Ground level, no building</li>
 *     <li>Level 1, 2, 3: Normal blocks, can be walked/built on (based on the rules)</li>
 *     <li>Dome: final block, cannot be built or walked on (no exceptions, with the implemented rules)</li>
 * </ul>
 */
public enum Block {
    LEVEL0(0),
    LEVEL1(1),
    LEVEL2(2),
    LEVEL3(3),
    DOME(4);

    private final int height;

    /**
     * Default constructor
     * <p>
     * Creates a block by its height.
     * </p>
     *
     * @param height the height (int value, 0 to 4) of the block to create
     */
    Block(int height) {
        this.height = height;
    }

    /**
     * <i>height</i>> getter
     *
     * @return the height (integer value) og the building
     */
    public int getHeight() {
        return height;
    }

}
