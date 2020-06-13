package it.polimi.ingsw.view.cli.console.graphics;


import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.Dialog;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;

/**
 * A dialog showing an error message and a button to close it
 */
public final class MessageDialog extends Dialog {

    /**
     * Default constructor
     * <p>
     * Creates a custom size dialog screen and prints it in the middle of the scene; if other dialogs are already showing, the
     * new dialog is created at an offset from the already existing one
     *
     * @param message the error message
     */
    public MessageDialog(String message, Window caller) {
        super("Notification", message, caller, "MessageDialog");
    }

    public MessageDialog(String message, Window caller, int width, int height, CursorPosition initCoord) {
        super("Notification", message, width, height, initCoord, caller, "MessageDialog");
    }

    /**
     * Removes the message dialog
     *
     * @see Dialog#remove()
     */
    @Override
    public void onCarriageReturn() {
       onQuit();
    }

    @Override
    public void onQuit() {
        this.remove();
    }
}
