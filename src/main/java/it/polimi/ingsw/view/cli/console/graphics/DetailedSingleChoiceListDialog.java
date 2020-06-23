package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.graphics.components.DetailPane;
import it.polimi.ingsw.view.cli.console.graphics.components.ReturningListItem;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * A dialog containing a list of possible options, each having a description, which is shown when the <i>cursor</i>
 * passes on the item
 */
public final class DetailedSingleChoiceListDialog extends SingleChoiceListDialog {

    private final DetailPane detailPane;
    private final Map<String, LinkedList<String>> options;

    /**
     * Default constructor
     * <p>
     * Creates a DetailedListChoiceDialog, which dimensions are adapted on the options provided;
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     * @param options the options to show and their details
     */
    public DetailedSingleChoiceListDialog(String title, String message, Window caller,
                                          Map<String, LinkedList<String>> options) {
        super(title, message, caller, new ArrayList<>(options.keySet()));
        this.options = options;
        detailPane = new DetailPane(this, 30, 20, "DetailPane");
        detailPane.getInitCoord().setCoordinates(this.getInitCoord().getRow() + 1, this.getInitCoord().getCol() + this.getWidth() - 2);
        addPassiveItem(detailPane);
    }

    /**
     * Draws itself and the details pane
     */
    @Override
    public void show() {
        super.show();
        refreshPane();
    }

    /**
     * Prints the currently selected item details on the side pane
     */
    private void refreshPane() {
        try {
            String currentSelection = ((ReturningListItem) currentActiveItem()).getItem();
            detailPane.refresh(detailPane.adaptStrings(new ArrayList<>(options.get(currentSelection))));
        } catch (ClassCastException e) {
            // do nothing
        }
    }

    /**
     * Selects the next ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowRight() {
        super.onArrowRight();
        refreshPane();
    }

    /**
     * Selects the previous ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowLeft() {
        super.onArrowLeft();
        refreshPane();
    }

    /**
     * Selects the next ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onTab() {
        super.onTab();
        refreshPane();
    }

    /**
     * Selects the previous ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowUp() {
        super.onArrowUp();
        refreshPane();
    }

    /**
     * Selects the next ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowDown() {
        super.onArrowDown();
        refreshPane();
    }
}
