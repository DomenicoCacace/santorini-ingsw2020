package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.ActiveItem;
import it.polimi.ingsw.view.cli.console.graphics.components.ReturningListItem;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;
import it.polimi.ingsw.view.cli.console.graphics.components.WindowItem;

import java.util.List;

public class SingleChoiceListPane extends ActiveItem {

    /**
     * Custom constructor
     * <p>
     * Creates an item using custom settings
     * <br>
     * The initial coordinates are relative to the object's parent
     *
     * @param parent    the Dialog containing this
     * @param initCoord the item coordinates
     * @param width     the object width
     * @param height    the object height
     * @param ID        the object ID
     */
    public SingleChoiceListPane(WindowItem parent, CursorPosition initCoord, int width, int height, List<String> options, String ID) {
        super(parent, initCoord, width, height, ID);
        int colOff = findCenter(this.getWidth(), options.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0));
        options.forEach(s -> addInteractiveItem(new ReturningListItem(this, new CursorPosition(7 + options.indexOf(s), colOff), s, options.indexOf(s) + 1)));
    }

    /**
     * <i>cli</i> getter
     *
     * @return the parent's CLI attribute
     */
    @Override
    protected CLI getCli() {
        return ((Window) parent).getCli();
    }

    /**
     * Defines the object behaviour when selected
     */
    @Override
    public void onSelect() {

    }

    /**
     * Defines the object behaviour when released
     */
    @Override
    public void onRelease() {

    }

    /**
     * Draws itself
     */
    @Override
    public void show() {
        super.show();
        activeItems.forEach(ActiveItem::show);
        currentInteractiveItem().onSelect();
    }

    /**
     * Selects the next InteractiveItem on the Dialog
     */
    @Override
    public void onArrowRight() {
        currentInteractiveItem().onRelease();
        nextInteractiveItem().onSelect();
    }

    /**
     * Selects the previous InteractiveItem on the Dialog
     */
    @Override
    public void onArrowLeft() {
        currentInteractiveItem().onRelease();
        previousInteractiveItem().onSelect();
    }

    /**
     * Performs an action based on the currently selected interactiveItem
     */
    @Override
    public void onCarriageReturn() {
        currentInteractiveItem().onCarriageReturn();
    }

    /**
     * Selects the next InteractiveItem on the Dialog
     */
    @Override
    public void onTab() {
        onArrowRight();
    }

    /**
     * Selects the previous InteractiveItem on the Dialog
     */
    @Override
    public void onArrowUp() {
        onArrowLeft();
    }

    /**
     * Selects the next InteractiveItem on the Dialog
     */
    @Override
    public void onArrowDown() {
        onArrowRight();
    }
}
