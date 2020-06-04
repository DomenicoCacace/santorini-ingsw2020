package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public final class MultipleChoiceListDialog extends InputDialog {

    private final int numberOfChoices;
    private final DetailPane detailPane;
    private final ClosingButton button;
    private final LinkedHashMap<String, LinkedList<String>> listOptions;


    /**
     * Default constructor
     * <p>
     * Creates a MultipleChoiceListDialog.xml, which dimensions are adapted on the options provided;
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param title           the dialog title
     * @param message         the dialog message
     * @param caller          the window which invoked this
     * @param detailedOptions the options to show and their details
     * @param numberOfChoices the number of items to select
     */
    public MultipleChoiceListDialog(String title, String message, Window caller, LinkedHashMap<String, LinkedList<String>> detailedOptions, int numberOfChoices) {
        super(title, message, width(new ArrayList<>(detailedOptions.keySet())), 12 + detailedOptions.size(), caller);
        this.numberOfChoices = numberOfChoices;
        listOptions = detailedOptions;
        List<String> options = new ArrayList<>(detailedOptions.keySet());
        int colOff = findCenter(this.getWidth(), options.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0));

        options.forEach(s -> addInteractiveItem(new ListItem(this, new CursorPosition(5 + options.indexOf(s), colOff), s, options.indexOf(s) + 1)));

        button = new ClosingButton(this, new CursorPosition(7 + options.size(), colOff), "Confirm");
        addInteractiveItem(button);
        detailPane = new DetailPane(this, 37, 20);
        detailPane.getInitCoord().setCoordinates(this.getInitCoord().getRow() + 1, this.getInitCoord().getCol() + this.getWidth() - 2);
        addNonInteractiveItem(detailPane);
    }

    /**
     * Determines the dialog width based on the number of options
     *
     * @param options the list of options
     * @return the adapted dialog size
     */
    protected static int width(List<String> options) {
        return Math.max(20, options.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0) + 8) + 10;
    }

    /**
     * Determines the Dialog behaviour when it is asked to close itself
     * <p>
     * For a MultipleChoiceListDialog, all of its inputs are passed to the inputManager
     * </p>
     */
    @Override
    public void onQuit() {
        remove();

        for (InteractiveItem item : inputs.keySet()) {
            String retVal = inputs.get(item);
            cli.evaluateInput(retVal);

            int actualVal = Integer.parseInt(retVal);

            for (InteractiveItem key : inputs.keySet()) {
                if (Integer.parseInt(inputs.get(key)) > actualVal)
                    inputs.replace(key, String.valueOf(Integer.parseInt(inputs.get(key)) - 1));
            }
        }
    }

    /**
     * Determines if the dialog can be closed
     * <p>
     * For a MultipleChoiceListDialog.xml, it can be closed when the number of selected items matches the number of requested
     * inputs
     *
     * @return true if the dialog can be closed, false otherwise
     */
    @Override
    public boolean canBeClosed() {
        int valid = 0;
        for (String val : inputs.values()) {
            if (val != null) {
                if (!(val.isBlank() && val.isEmpty()))
                    valid++;
            }
        }
        return valid == numberOfChoices;
    }

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
            String currentSelection = ((ListItem) currentInteractiveItem()).getItem();
            detailPane.refresh(detailPane.adaptStrings(new ArrayList<>(listOptions.get(currentSelection))));
        } catch (ClassCastException e) {
            // do nothing
        }
    }

    /**
     * Selects/deselects list items or confirms the selection, based on the currentItem
     */
    @Override
    public void onCarriageReturn() {
        if (currentInteractiveItem().equals(button)) {
            button.onCarriageReturn();
        } else {  // it must be a ListItem, the cast is legit
            currentInteractiveItem().onCarriageReturn();
            if (inputs.size() > numberOfChoices) {
                inputs.remove(currentInteractiveItem());
                ((ListItem) currentInteractiveItem()).deselect();
            }
        }
    }

    /**
     * Selects the next InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowRight() {
        super.onArrowRight();
        refreshPane();
    }

    /**
     * Selects the previous InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowLeft() {
        super.onArrowLeft();
        refreshPane();
    }

    /**
     * Selects the next InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onTab() {
        super.onTab();
        refreshPane();
    }

    /**
     * Selects the previous InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowUp() {
        super.onArrowUp();
        refreshPane();
    }

    /**
     * Selects the next InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowDown() {
        super.onArrowDown();
        refreshPane();
    }
}
