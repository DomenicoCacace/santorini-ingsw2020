package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;

public class SimpleListItem extends ActiveItem {

    /**
     * The item string
     */
    protected final String item;

    /**
     * Determines if this has been selected in the parent
     */
    protected boolean selected;

    /**
     * Default constructor
     * <p>
     * Creates an item using its default settings;
     * <br>
     * The initial coordinates are relative to the object's parent
     *
     * @param parent    the Window containing this
     * @param initCoord the item coordinates
     * @param item        the object ID
     */
    public SimpleListItem(WindowItem parent, CursorPosition initCoord, String item) {
        super(parent, initCoord, item);
        this.item = item;
        selected = false;
    }

    /**
     * <i>item</i> getter
     *
     * @return the ListItem item
     */
    public String getItem() {
        return item;
    }

    /**
     * Highlights the item, using a light color scheme
     */
    protected void highlight() {
        Console.out.printAt(CursorPosition.offset(initCoord, 0, 1), color.light + " " + item);
    }

    /**
     * Brings the item to its initial state, using the parent's background color
     */
    public void deselect() {
        selected = false;
    }

    /**
     * Prints the object on the screen
     */
    @Override
    public void show() {
        if (selected)
            highlight();
        else
            Console.out.printAt(CursorPosition.offset(initCoord, 0, 1), getBackgroundColor() + " " + item);
    }

    /**
     * Highlights the item, using a light color scheme
     */
    @Override
    public void onSelect() {
        highlight();
    }

    /**
     * <i>Brings back</i> the item
     */
    @Override
    public void onRelease() {
        if (!selected)
            show();
    }
}
