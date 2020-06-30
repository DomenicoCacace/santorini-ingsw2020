package it.polimi.ingsw.client.view.cli.console.graphics.components;

import it.polimi.ingsw.client.view.cli.console.CursorPosition;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class InputDialog extends Dialog {

    /**
     * Maps all the activeItems which
     */
    protected final HashMap<ActiveItem, String> inputs;

    /**
     * Default constructor
     * <p>
     * Creates a standard size inputDialog window, using the properties file, without printing it;
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one.
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     */
    public InputDialog(String title, String message, Window caller) {
        super(title, message, caller);
        inputs = new LinkedHashMap<>();
    }

    /**
     * Custom constructor
     * <p>
     * Creates a custom size input dialog window, without printing it; if other dialogs are already showing, the
     * new dialog is created at an offset from the already existing one
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param height  the dialog height
     * @param width   the dialog width
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     */
    public InputDialog(String title, String message, int width, int height, Window caller) {
        super(title, message, width, height, caller);
        inputs = new LinkedHashMap<>();
    }

    /**
     * Custom constructor
     * <p>
     * Creates a custom size input dialog window, without printing it;
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param height  the dialog height
     * @param width   the dialog width
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     */
    public InputDialog(String title, String message, int width, int height, CursorPosition initCoord, Window caller) {
        super(title, message, width, height, initCoord, caller);
        inputs = new LinkedHashMap<>();
    }

    /**
     * Provides a map containing active items and their corresponding values to be returned
     *
     * @return the up mentioned map
     */
    public Map<ActiveItem, String> getInputs() {
        return inputs;
    }

    /**
     * Determines if the dialog can be closed
     * <p>
     * For a InputDialog, it can be closed when all the required input fields have been filled
     *
     * @return true if the dialog can be closed, false otherwise
     */
    @Override
    public boolean canBeClosed() {
        int valid = 0;
        for (String val : inputs.values()) {
            if (val != null && !(val.isBlank() && val.isEmpty()))
                valid++;
        }
        return valid == inputs.keySet().size();
    }

    /**
     * Determines the Dialog behaviour when it is asked to close itself
     * <p>
     * For an InputDialog, all of its inputs are passed to the inputManager
     * </p>
     */
    @Override
    public void onQuit() {
        remove();
        inputs.values().forEach(cli::evaluateInput);
    }

    /**
     * Provides the first {@linkplain #activeItems} textColor
     *
     * @return the first item's text color scheme
     */
    @Override
    public String getTextColor() {
        return activeItems.getFirst().getTextColor();
    }

    /**
     * Draws itself
     */
    @Override
    public void show() {
        super.show();
        activeItems.forEach(ActiveItem::show);
        currentActiveItem().onSelect();
    }

    /**
     * Selects the next ActiveItem on the Dialog
     */
    @Override
    public void onArrowRight() {
        currentActiveItem().onRelease();
        nextActiveItem().onSelect();
    }

    /**
     * Selects the previous ActiveItem on the Dialog
     */
    @Override
    public void onArrowLeft() {
        currentActiveItem().onRelease();
        previousActiveItem().onSelect();
    }

    /**
     * Performs an action based on the currently selected ActiveItem
     */
    @Override
    public void onCarriageReturn() {
        currentActiveItem().onCarriageReturn();
    }

    /**
     * Selects the next ActiveItem on the Dialog
     */
    @Override
    public void onTab() {
        onArrowRight();
    }

    /**
     * Selects the previous ActiveItem on the Dialog
     */
    @Override
    public void onArrowUp() {
        onArrowLeft();
    }

    /**
     * Selects the next ActiveItem on the Dialog
     */
    @Override
    public void onArrowDown() {
        onArrowRight();
    }
}
