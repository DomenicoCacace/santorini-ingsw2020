package it.polimi.ingsw.view.cli.console.graphics.components;


import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.KeyEventListener;


/**
 * An abstraction for any object containable in a Window
 */
public abstract class ActiveItem extends WindowItem implements Toggleable {


    /**
     * Default constructor
     * <p>
     * Creates an interactive item using its default settings;
     * <br>
     * The initial coordinates are relative to the object's parent
     *
     * @param parent    the Window containing this
     * @param initCoord the item
     * @param ID the object ID
     */
    protected ActiveItem(WindowItem parent, CursorPosition initCoord, String ID) {
        super(parent, initCoord, ID);
    }

    /**
     * Custom constructor
     * <p>
     * Creates an interactive item using custom settings
     * <br>
     * The initial coordinates are relative to the object's parent
     *
     * @param parent    the Window containing this
     * @param initCoord the item coordinates
     * @param width     the object width
     * @param height    the object height
     * @param ID the object ID
     */
    protected ActiveItem(Window parent, CursorPosition initCoord, int width, int height, String ID) {
        super(parent, initCoord, width, height, ID);
    }

    /**
     * <i>background</i> getter
     *
     * @return the <b>parent's</b> background color
     */
    public String getBackgroundColor() {
        return parent.getBackgroundColor();
    }

    /**
     * Defines the object behaviour when selected
     */
    public abstract void onSelect();

    /**
     * Defines the object behaviour when released
     */
    public abstract void onRelease();

    /**
     * Enables the component
     */
    @Override
    public void enable() {
        Console.in.addKeyEventListener(this);
    }

    /**
     * Disables the component
     */
    @Override
    public void onDisable() {
        Console.in.removeKeyEventListener(this);
    }
}
