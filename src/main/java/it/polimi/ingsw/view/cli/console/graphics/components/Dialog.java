package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.RawConsoleOutput;
import java.util.List;

import static it.polimi.ingsw.view.Constants.*;

/**
 * General abstraction for a dialog window
 */
public abstract class Dialog extends Window {

    /**
     * The dialog title
     */
    protected final String title;

    /**
     * The dialog message
     */
    protected final String message;


    /**
     * Default constructor
     * <p>
     * Creates a standard size dialog window, using the properties file, without printing it;
     * if other dialogs are already showing, the new dialog is created at an offset from the already existing one.
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @param parent  the window which invoked this
     * @param ID the object ID
     */
    protected Dialog(String title, String message, Window parent, String ID) {
        super(parent, ID);
        this.title = title;
        this.message = message;
        Console.in.disableConsoleInput();
    }

    /**
     * Custom constructor
     * <p>
     * Creates a custom size dialog window, without printing it; if other dialogs are already showing, the
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
    protected Dialog(String title, String message, int width, int height, Window caller, String ID) {
        super(caller, width, height, new CursorPosition(findCenter(caller.getHeight(), height), findCenter(caller.getWidth(), width)), ID);
        this.title = title;
        this.message = message;
        Console.in.disableConsoleInput();
    }

    /**
     * Custom constructor
     * <p>
     * Creates a custom size dialog window, without printing it;
     * <br>
     * The console echo is disabled as soon as the Dialog is created
     *
     * @param height  the dialog height
     * @param width   the dialog width
     * @param title   the dialog title
     * @param message the dialog message
     * @param caller  the window which invoked this
     * @param ID the object ID
     */
    protected Dialog(String title, String message, int width, int height, CursorPosition position, Window caller, String ID) {
        super(caller, width, height, position, ID);
        this.title = title;
        this.message = message;
        Console.in.disableConsoleInput();
    }


    /**
     * Determines if the dialog can be closed; by default, a dialog can be closed anytime
     *
     * @return true if the dialog can be closed, false otherwise
     */
    public boolean canBeClosed() {
        return true;
    }

    /**
     * Determines the Dialog behaviour when it is asked to close itself
     */
    public abstract void onQuit();


    /**
     * Draws the dialog title
     */
    protected void drawTitle() {
        int center = findCenter(width, title.length());
        Console.out.printAt(CursorPosition.offset(initCoord, 0, center), color.getDark() + " " + title + " ");
    }

    /**
     * Draws the dialog message, splitting it on multiple lines if necessary
     */
    protected void drawMessage() {
        List<String> lines = RawConsoleOutput.splitMessage(message, width - 2);
        for (int i = 0; i < lines.size(); i++) {
            int center = findCenter(width, lines.get(i).length());
            Console.out.printAt(CursorPosition.offset(initCoord, 2 + i, center), color.getDark() + lines.get(i));
        }
    }

    /**
     * <i>background</i> getter
     *
     * @return the <b>parent's</b> background color
     */
    public String getBackgroundColor() {
        return color.backgroundDark;
    }

    /**
     * Draws the dialog background
     */
    protected void drawBackground() {
        Console.out.drawMatrix(background, initCoord);
    }

    /**
     * Draws right and bottom shadows
     * <br>
     * Uses the caller's color as background, because it looks better
     */
    protected void drawShadows() {
        for (int row = 1; row < height - 1; row++)
            Console.out.printAt(CursorPosition.offset(initCoord, row, width), parent.getBackgroundColor() + LINE_SHADOW_RIGHT);

        for (int col = 1; col < width; col++)
            Console.out.printAt(CursorPosition.offset(initCoord, height, col), parent.getBackgroundColor() + LINE_SHADOW_BOTTOM);

        Console.out.printAt(CursorPosition.offset(initCoord, height, 0), color.getLight());
        Console.out.printAt(CursorPosition.offset(initCoord, height, width), parent.getBackgroundColor() + LINE_SHADOW_CORNER);
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width), parent.getBackgroundColor() + LINE_SHADOW_RIGHT);
    }


    /**
     * Draws itself
     */
    @Override
    public void show() {
        super.show();
        drawBorders();
        drawShadows();
        drawMessage();
        drawTitle();
        if (activeItems.size() > 0) {
            activeItems.forEach(ActiveItem::show);
            currentInteractiveItem().onSelect();
        }
        if (passiveItems.size() > 0) {
            passiveItems.forEach(WindowItem::show);
        }
    }

    /**
     * Removes itself and eventually re-enables the console input
     */
    @Override
    public void remove() {
        super.remove();
        if (passiveItems.size() > 0)
            passiveItems.forEach(WindowItem::remove);
        if (activeItems.size() > 0)
            activeItems.forEach(ActiveItem::remove);
        if (enableInputOnReturn)
            Console.in.enableConsoleInput();
        Console.closeWindow(this);
    }
}
