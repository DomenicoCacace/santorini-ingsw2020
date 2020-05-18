package it.polimi.ingsw.model;

/**
 * Worker color
 * <p>
 * The workers' pawn color.
 * </p>
 */
public enum Color {
    BLUE(0),
    RED(1),
    PURPLE(2);

    private final int colorID;

    Color(int colorID) {
        this.colorID = colorID;
    }
}
