package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.CursorPosition;

/**
 * A single list item, returning a value when <i>clicked</i>
 */
public class ReturningListItem extends ListItem {

    /**
     * Default constructor
     *
     * @param parent    the item container
     * @param initCoord the initial coordinates
     * @param item      the item's string
     */
    public ReturningListItem(InputDialog parent, CursorPosition initCoord, String item, int retVal) {
        super(parent, initCoord, item, retVal);
    }

    /**
     * Closes its parent and makes the inputManager evaluate its return value
     */
    @Override
    public void onCarriageReturn() {
        parent.remove();
        ((InputDialog) parent).getCli().evaluateInput(String.valueOf(retVal));
    }
}
