package it.polimi.ingsw.model;

public enum Color {
    BLUE(0),
    RED(1),
    GREEN(2);

    private final int colorID;

    Color(int colorID) {
        this.colorID = colorID;
    }
}
