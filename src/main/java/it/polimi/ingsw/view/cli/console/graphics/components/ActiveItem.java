package it.polimi.ingsw.view.cli.console.graphics.components;


import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;


/**
 * An abstraction for any object containable in a Window
 */
public abstract class ActiveItem extends WindowItem implements Toggleable {

    /**
     * Default constructor
     * <p>
     * Creates an active item using its default settings;
     * <br>
     * The initial coordinates are relative to the object's parent
     *
     * @param parent    the Window containing this
     * @param initCoord the item
     * @param id the object id
     */
    protected ActiveItem(WindowItem parent, CursorPosition initCoord, String id) {
        super(parent, initCoord, id);
    }

    /**
     * Custom constructor
     * <p>
     * Creates an active item using custom settings
     * <br>
     * The initial coordinates are relative to the object's parent
     *
     * @param parent    the Window containing this
     * @param initCoord the item coordinates
     * @param width     the object width
     * @param height    the object height
     * @param id the object id
     */
    protected ActiveItem(WindowItem parent, CursorPosition initCoord, int width, int height, String id) {
        super(parent, initCoord, width, height, id);
    }

    /**
     * <i>background</i> getter
     *
     * @return the <b>parent's</b> background color
     */
    @Override
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
