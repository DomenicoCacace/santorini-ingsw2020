package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.CursorPosition;

/**
 * A <i>clickable</i> button which returns a String value when <i>clicked</i>
 */
public class ReturningButton extends ClosingButton {

    private final String retVal;

    /**
     * Default constructor
     * <p>
     * Creates a new Button object, using its default properties
     *
     * @param parent    the button container; must be an InputDialog (or subclass)
     * @param initCoord the initial coordinates, relative to the container
     * @param text      the button text
     * @param retVal    the value to return when clicked
     */
    public ReturningButton(InputDialog parent, CursorPosition initCoord, String text, String retVal) {
        super(parent, initCoord, text);
        this.retVal = retVal;
    }

    /**
     * Custom constructor
     * <p>
     * Creates a new Button object, using custom dimensions
     *
     * @param parent    the button container; must be an InputDialog (or subclass)
     * @param initCoord the initial coordinates, relative to the container
     * @param width     the button width
     * @param height    the button height
     * @param text      the button text
     * @param retVal    the value to return when clicked
     */
    public ReturningButton(InputDialog parent, CursorPosition initCoord, int width, int height, String text, String retVal) {
        super(parent, initCoord, width, height, text);
        this.retVal = retVal;
    }

    /**
     * Highlights the button, drawing its shadows
     */
    @Override
    public void onSelect() {
        super.onSelect();
        ((InputDialog) parent).getInputs().put(this, this.retVal);
    }

    /**
     * Releases the button, removing the shadow highlighting the selection
     */
    @Override
    public void onRelease() {
        super.onRelease();
        ((InputDialog) parent).getInputs().remove(this, this.retVal);
    }
}
