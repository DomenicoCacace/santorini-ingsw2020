package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;

/**
 * A <i>clickable</i> button
 */
public abstract class Button extends InteractiveItem {

    private final String text;

    /**
     * Default constructor
     * <p>
     * Creates a new Button object, using its default properties
     *
     * @param parent    the button container
     * @param initCoord the initial coordinates, relative to the container
     * @param text      the button text
     */
    protected Button(Dialog parent, CursorPosition initCoord, String text) {
        super(parent, initCoord, text.length() + 4, 3);
        this.text = text;
    }

    /**
     * Custom constructor
     * <p>
     * Creates a new Button object, using custom settings
     *
     * @param parent    the button container
     * @param initCoord the initial coordinates, relative to the container
     * @param width     the button width
     * @param height    the button height
     * @param text      the button text
     */
    protected Button(Dialog parent, CursorPosition initCoord, int width, int height, String text) {
        super(parent, initCoord, width, height);
        this.text = text;
    }

    /**
     * Draws the button text
     */
    protected void drawText() {
        int center = Rectangle.findCenter(width, text.length());
        Console.out.printAt(CursorPosition.offset(initCoord, 1, center), color.dark + text);
    }

    /**
     * Shows the button on the screen
     */
    @Override
    protected void show() {
        super.show();
        drawText();
        drawBorders();
    }

    /**
     * Highlights the button, drawing its shadows
     */
    @Override
    public void onSelect() {
        Console.in.disableConsoleInput();
        show();
        drawShadows();
        Console.placeCursor(CursorPosition.maxCursor.getRow(), 0);
    }

    /**
     * Releases the button, removing the shadow highlighting the selection
     */
    @Override
    public void onRelease() {
        hideShadows();
    }

}
