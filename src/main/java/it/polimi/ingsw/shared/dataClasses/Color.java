package it.polimi.ingsw.shared.dataClasses;

/**
 * Worker color
 * <p>
 * The workers' pawn color.
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
