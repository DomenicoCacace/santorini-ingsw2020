package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.KeyEventListener;
import it.polimi.ingsw.view.cli.console.graphics.components.*;

import java.util.*;

public class ListPane extends ActiveItem implements KeyEventListener {

    private final Map<String, List<String>> options;
    private final DetailPane detailPane;

    public ListPane(Window caller, CursorPosition initCoord, int width, int height, Map<String, List<String>> options, String ID) {
        super(caller, initCoord, width, height, ID);
        this.options = options;

        int colOff = findCenter(this.getWidth(), options.keySet().stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0));

        List<String> items = new ArrayList<>(options.keySet());
        items.forEach(s -> addInteractiveItem(new SimpleListItem(this, new CursorPosition(7 + items.indexOf(s), colOff), s)));
        detailPane = new DetailPane(this, width, height, "DetailPane");
        detailPane.getInitCoord().setCoordinates(this.getInitCoord().getRow() + height + 1, this.getInitCoord().getCol());
        addNonInteractiveItem(detailPane);
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
            String currentSelection = ((SimpleListItem) currentInteractiveItem()).getItem();
            detailPane.refresh(detailPane.adaptStrings(new ArrayList<>(options.get(currentSelection))));
        } catch (ClassCastException e) {
            // do nothing
        }
    }

    /**
     * Selects the next InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowRight() {
        currentInteractiveItem().onRelease();
        nextInteractiveItem().onSelect();
        refreshPane();
    }

    /**
     * Selects the previous InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowLeft() {
        currentInteractiveItem().onRelease();
        previousInteractiveItem().onSelect();
        refreshPane();
    }


    /**
     * Selects the previous InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowUp() {
        currentInteractiveItem().onRelease();
        previousInteractiveItem().onSelect();
        refreshPane();
    }

    /**
     * Selects the next InteractiveItem on the Dialog and refreshes the details
     */
    @Override
    public void onArrowDown() {
        currentInteractiveItem().onRelease();
        nextInteractiveItem().onSelect();
        refreshPane();
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

    /**
     * Disables the component
     */
    @Override
    public void onDisable() {
        this.hideBorders();
    }
}
