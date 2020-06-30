package it.polimi.ingsw.client.view.cli.console.graphics;


import it.polimi.ingsw.client.view.cli.console.CursorPosition;
import it.polimi.ingsw.client.view.cli.console.graphics.components.ClosingButton;
import it.polimi.ingsw.client.view.cli.console.graphics.components.Dialog;
import it.polimi.ingsw.client.view.cli.console.graphics.components.Window;

/**
 * A dialog showing an error message and a button to close it
 */
public final class ErrorDialog extends Dialog {

    /**
     * Default constructor
     * <p>
     * Creates a custom size dialog screen and prints it in the middle of the scene; if other dialogs are already showing, the
     * new dialog is created at an offset from the already existing one
     *
     * @param message the error message
     */
    public ErrorDialog(String message, Window caller) {
        super("Error", message, caller);
        int colOff = findCenter(this.getWidth(), 10);
        int rowOff = findCenter(this.getHeight(), 3) * 9 / 5;
        addActiveItem(new ClosingButton(this, new CursorPosition(rowOff, colOff), "OK"));
    }

    public ErrorDialog(String message, Window caller, int width, int height, CursorPosition initCoord) {
        super("Error", message, width, height, initCoord, caller);
        int colOff = findCenter(this.getWidth(), 10);
        int rowOff = findCenter(this.getHeight(), 3) * 9 / 5;
        addActiveItem(new ClosingButton(this, new CursorPosition(rowOff, colOff), "OK"));
    }

    /**
     * Removes the error dialog
     *
     * @see Dialog#remove()
     */
    @Override
    public void onCarriageReturn() {
        currentActiveItem().onCarriageReturn();
    }

    /**
     * Removes this dialog
     */
    @Override
    public void onQuit() {
        remove();
    }
}
