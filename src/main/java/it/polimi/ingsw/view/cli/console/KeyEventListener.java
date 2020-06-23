package it.polimi.ingsw.view.cli.console;

public interface KeyEventListener {

    /**
     * Defines the listener behavior when a printable character key is pressed
     */
    default void onPrintableKey(char key) {
    }

    /**
     * Defines the listener behavior when the <code>Enter</code> key is pressed
     */
    default void onCarriageReturn() {
    }

    /**
     * Defines the listener behavior when the <code>Backspace</code> key is pressed
     */
    default void onBackspace() {
    }

    /**
     * Defines the listener behavior when the <code>Tab</code> key is pressed
     */
    default void onTab() {
    }

    /**
     * Defines the listener behavior when the <code>Arrow Up</code> key is pressed
     */
    default void onArrowUp() {
    }

    /**
     * Defines the listener behavior when the <code>Arrow Down</code> key is pressed
     */
    default void onArrowDown() {
    }

    /**
     * Defines the listener behavior when the <code>Arrow Right</code> key is pressed
     */
    default void onArrowRight() {
    }

    /**
     * Defines the listener behavior when the <code>Arrow Left</code> key is pressed
     */
    default void onArrowLeft() {
    }


}
