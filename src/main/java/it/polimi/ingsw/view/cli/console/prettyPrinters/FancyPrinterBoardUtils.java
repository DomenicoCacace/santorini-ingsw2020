package it.polimi.ingsw.view.cli.console.prettyPrinters;

import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.KeyEventListener;
import it.polimi.ingsw.view.cli.console.graphics.SingleChoiceListDialog;
import it.polimi.ingsw.view.cli.console.graphics.GridOverlay;
import it.polimi.ingsw.view.cli.console.graphics.components.Toggleable;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;
import it.polimi.ingsw.view.cli.console.graphics.components.WindowItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Collection of WindowItems and utility methods to decorate the game board, giving information about the game status
 */
public class FancyPrinterBoardUtils extends Window implements KeyEventListener  {

    private final FancyPrinter printer;
    private final CursorPosition boardOffset;
    private final Map<Components, Toggleable> items;


    public FancyPrinterBoardUtils(FancyPrinter printer) throws IOException {
        super(printer.console);
        this.printer = printer;
        Console.addWindow(this);
        boardOffset = new CursorPosition(1, 1);
        this.items = new HashMap<>();
        items.put(Components.GRID_OVERLAY, new GridOverlay(printer.console, boardOffset, printer));

    }

    /**
     * <i>boardOffset</i> getter
     *
     * @return the board initial coordinates
     */
    public CursorPosition getBoardOffset() {
        return boardOffset;
    }

    /**
     * Enables the user to select a cell, using a {@linkplain GridOverlay}
     */
    public void enableGridInput() {
        items.forEach((k, c) ->{
            if (k.equals(Components.GRID_OVERLAY))
                c.enable();
            else
                c.onDisable();
        });
    }

    /**
     * Shows a dialog asking the user to select an action to perform
     *
     * @param actions a list of possible actions
     */
    public void enableActionSelector(List<PossibleActions> actions) {
        List<String> options = new ArrayList<>();
        actions.forEach(a -> options.add(a.toString()));
        new SingleChoiceListDialog("", "Select an action", this, options, 85, 9, new CursorPosition(54, 124));
    }

    /**
     * Defines the listener behavior when the <code>Enter</code> key is pressed
     */
    @Override
    public void onCarriageReturn() {
        for (Toggleable t : items.values()) {
            t.onDisable();
            Console.in.removeKeyEventListener(t);
        }
    }

    /**
     * Utilities unique names
     */
    private enum Components {
        GRID_OVERLAY
    }
}
