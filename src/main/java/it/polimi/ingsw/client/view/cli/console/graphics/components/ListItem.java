package it.polimi.ingsw.client.view.cli.console.graphics.components;

import it.polimi.ingsw.client.view.cli.console.CursorPosition;

/**
 * A single list item, returning a value when <i>clicked</i>
 */
public class ListItem extends SimpleListItem {

    /**
     * The item index in the parent's list
     */
    protected final int retVal;

    /**
     * Default constructor
     *
     * @param parent    the item container
     * @param initCoord the initial coordinates
     * @param item      the item's string
     */
    public ListItem(WindowItem parent, CursorPosition initCoord, String item, int retVal) {
        super(parent, initCoord, item);
        this.retVal = retVal;
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
