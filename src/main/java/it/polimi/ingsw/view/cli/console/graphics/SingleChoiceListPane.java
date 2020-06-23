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
     * @param id        the object id
     */
    public SingleChoiceListPane(WindowItem parent, CursorPosition initCoord, int width, int height, List<String> options, String id) {
        super(parent, initCoord, width, height, id);
        int colOff = findCenter(this.getWidth(), options.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0));
        options.forEach(s -> addActiveItem(new ReturningListItem(this, new CursorPosition(3 + options.indexOf(s),
                colOff), s, options.indexOf(s) + 1)));
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
        /*
         * Does nothing, as it is meant to take exclusive control of the input
         */
    }

    /**
     * Defines the object behaviour when released
     */
    @Override
    public void onRelease() {
        /*
         * Does nothing, as it is meant to take exclusive control of the input
         */
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
