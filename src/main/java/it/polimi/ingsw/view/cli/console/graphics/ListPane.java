package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.KeyEventListener;
import it.polimi.ingsw.view.cli.console.graphics.components.ActiveItem;
import it.polimi.ingsw.view.cli.console.graphics.components.DetailPane;
import it.polimi.ingsw.view.cli.console.graphics.components.SimpleListItem;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A pane showing a list of elements; when the enter key is pressed, the currently selected item is triggered, causing
 * it to return its value to the inputManager and this to be hidden by the container
 */
public class ListPane extends ActiveItem implements KeyEventListener {

    private final Map<String, List<String>> options;
    private final DetailPane detailPane;

    /**
     * Custom constructor
     * <p>
     * Creates a SimpleListChoiceDialog, using custom values for its width and height
     *
     * @param caller  the window which invoked this
     * @param initCoord the initial coordinates
     * @param width the pane width
     * @param height the pane height
     * @param options the options to show
     * @param id the item id
     */
    public ListPane(Window caller, CursorPosition initCoord, int width, int height, Map<String, List<String>> options, String id) {
        super(caller, initCoord, width, height, id);
        this.options = options;

        int colOff = findCenter(this.getWidth(), options.keySet().stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0));

        List<String> items = new ArrayList<>(options.keySet());
        items.forEach(s -> addActiveItem(new SimpleListItem(this, new CursorPosition(7 + items.indexOf(s), colOff), s)));
        detailPane = new DetailPane(this, width, height, "DetailPane");
        detailPane.getInitCoord().setCoordinates(this.getInitCoord().getRow(), this.getInitCoord().getCol());
        addPassiveItem(detailPane);
    }


    /**
     * Draws itself and the details pane
     */
    @Override
    public void show() {
        refreshPane();
    }

    /**
     * Prints the currently selected item details on the side pane
     */
    private void refreshPane() {
        try {
            String currentSelection = ((SimpleListItem) currentActiveItem()).getItem();
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
        nextActiveItem();
        refreshPane();
    }

    /**
     * Selects the previous ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowLeft() {
        previousActiveItem();
        refreshPane();
    }


    /**
     * Selects the previous ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowUp() {
        onArrowLeft();
    }

    /**
     * Selects the next ActiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowDown() {
        onArrowRight();
    }

    /**
     * Defines the listener behavior when the <code>Enter</code> key is pressed
     */
    @Override
    public void onCarriageReturn() {
        onDisable();
    }

    /**
     * Defines the object behaviour when selected
     */
    @Override
    public void onSelect() {
        drawBorders();
    }

    /**
     * Defines the object behaviour when released
     */
    @Override
    public void onRelease() {
        hideBorders();
    }

    /**
     * Enables the component
     */
    @Override
    public void enable() {
        this.drawBorders();
    }
}
