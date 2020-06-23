package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.ErrorDialog;

/**
 * A clickable button that closes its container (if possible) when pressed
 */
public class ClosingButton extends Button {

    /**
     * Default constructor
     * <p>
     * Creates a new Button object, using its default properties
     *
     * @param parent    the button container
     * @param initCoord the initial coordinates, relative to the container
     * @param text      the button text
     */
    public ClosingButton(Dialog parent, CursorPosition initCoord, String text) {
        super(parent, initCoord, text);
    }

    /**
     * Custom constructor
     * <p>
     * Creates a new Button object, using its default properties
     *
     * @param parent    the button container
     * @param initCoord the initial coordinates, relative to the container
     * @param text      the button text
     */
    public ClosingButton(Dialog parent, CursorPosition initCoord, int width, int height, String text) {
        super(parent, initCoord, width, height, text);
    }

    /**
     * Closes the parent, performing eventual operations defined by the caller's {@linkplain Dialog#onQuit()}.
     */
    @Override
    public void onCarriageReturn() {
        if (((Dialog) parent).canBeClosed())
            ((Dialog) parent).onQuit();
        else
            new ErrorDialog("Cannot close the dialog; make sure that all the fields have been filled and then retry", ((Dialog) parent)).show();
    }
}
