package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;

/**
 * A single list item, returning a value when <i>clicked</i>
 */
public class ListItem extends InteractiveItem {

    /**
     * The item string
     */
    protected final String item;

    /**
     * The item index in the parent's list
     */
    protected final int retVal;

    /**
     * Determines if this has been selected in the parent
     */
    private boolean selected;

    /**
     * Default constructor
     *
     * @param parent    the item container
     * @param initCoord the initial coordinates
     * @param item      the item's string
     */
    public ListItem(InputDialog parent, CursorPosition initCoord, String item, int retVal) {
        super(parent, initCoord);
        this.item = item;
        this.retVal = retVal;
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
    protected void show() {
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

    /**
     * Adds or removes this to the parent's inputs
     */
    @Override
    public void onCarriageReturn() {
        if (selected) {
            ((InputDialog) parent).inputs.remove(this);
            selected = false;
        } else {
            ((InputDialog) parent).inputs.put(this, String.valueOf(retVal));
            selected = true;
        }
    }
}
