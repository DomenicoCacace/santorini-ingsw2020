package it.polimi.ingsw.client.view.cli.console.graphics.components;


import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.CursorPosition;
import it.polimi.ingsw.client.view.cli.console.RawConsoleOutput;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.view.Constants.*;

/**
 * A window showing details about an item in its parent
 */
public class DetailPane extends WindowItem implements LiveRefreshItemInterface {


    /**
     * Default constructor
     * <p>
     * Creates an item using its default settings;
     * <br>
     * The initial coordinates are relative to the object's parent
     *  @param parent the Window containing this
     *
     */
    @SuppressWarnings("unused")
    public DetailPane(Window parent) {
        super(parent, new CursorPosition());
        initCoord.setCoordinates(parent.getInitCoord().getRow(), parent.initCoord.getCol() + parent.getWidth());
    }

    /**
     * Custom constructor
     * <p>
     * Creates an item using custom settings
     * <br>
     * The initial coordinates are relative to the object's parent
     *  @param parent the Dialog containing this
     * @param width  the object width
     * @param height the object height
     */
    public DetailPane(WindowItem parent, int width, int height) {
        super(parent, new CursorPosition(), width, height);
        initCoord.setCoordinates(parent.getInitCoord().getRow(), parent.initCoord.getCol() + parent.getWidth());
    }

    /**
     * Draws right and bottom shadows
     * <br>
     * Uses the parent's parent color scheme, to merge seamlessly with the background
     */
    @Override
    protected void drawShadows() {
        for (int row = 1; row < height - 1; row++)
            Console.out.printAt(CursorPosition.offset(initCoord, row, width), parent.getParent().getColor().getBackgroundDark() + LINE_SHADOW_RIGHT);

        for (int col = 1; col < width; col++)
            Console.out.printAt(CursorPosition.offset(initCoord, height, col), parent.getParent().getColor().getBackgroundDark() + LINE_SHADOW_BOTTOM);

        Console.out.printAt(CursorPosition.offset(initCoord, height, 0), parent.getParent().getColor().getLight());
        Console.out.printAt(CursorPosition.offset(initCoord, height, width), parent.getParent().getColor().getBackgroundDark() + LINE_SHADOW_CORNER);
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width), parent.getParent().getColor().getBackgroundDark() + LINE_SHADOW_RIGHT);
    }

    /**
     * Refreshes its content
     *
     * @param info the details
     */
    @Override
    public void refresh(List<String> info) {
        show();
        Console.cursor.setCoordinates(initCoord.getRow() + 2, initCoord.getCol() + 2);
        Console.cursor.moveCursorTo();
        Console.cursor.setAsHome();
        for (String s : info) {
            Console.out.printlnAt(Console.cursor, s);
        }
    }

    /**
     * Adapts the list of details to the pane size, creating empty lines if necessary
     *
     * @param details the list o f details
     */
    public List<String> adaptStrings(List<String> details) {
        List<String> adaptedDetails = new ArrayList<>();
        details.forEach(d -> {
            if (d.equals("\n") || d.isBlank() || d.isEmpty())
                adaptedDetails.add("");
            else
                adaptedDetails.addAll(RawConsoleOutput.splitMessage(d, this.width - 4));
        });
        return adaptedDetails;
    }
}
