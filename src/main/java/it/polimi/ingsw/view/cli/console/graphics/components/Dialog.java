package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.RawConsoleOutput;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
     * A list containing all the interactive items
     */
    protected final Deque<InteractiveItem> interactiveItems;

    /**
     * A list containing all non-interactive items (e.g. {@linkplain DetailPane})
     */
    protected final List<WindowItem> nonInteractiveItems;


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
     */
    protected Dialog(String title, String message, Window parent) {
        super(parent);
        this.title = title;
        this.message = message;
        this.interactiveItems = new ArrayDeque<>();
        this.nonInteractiveItems = new ArrayList<>();
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
    protected Dialog(String title, String message, int width, int height, Window caller) {
        super(caller, width, height, new CursorPosition(findCenter(caller.getHeight(), height), findCenter(caller.getWidth(), width)));
        this.title = title;
        this.message = message;
        this.interactiveItems = new ArrayDeque<>();
        this.nonInteractiveItems = new ArrayList<>();
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
     */
    protected Dialog(String title, String message, int width, int height, CursorPosition position, Window caller) {
        super(caller, width, height, position);
        this.title = title;
        this.message = message;
        this.interactiveItems = new ArrayDeque<>();
        this.nonInteractiveItems = new ArrayList<>();
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
     * Adds a new interactive item to the items list
     *
     * @param item the item to add
     */
    protected void addInteractiveItem(InteractiveItem item) {
        interactiveItems.addLast(item);
    }

    /**
     * Adds a new item to the items list
     *
     * @param item the item to add
     */
    protected void addNonInteractiveItem(WindowItem item) {
        nonInteractiveItems.add(item);
    }

    /**
     * Provides the dialog item which is currently selected
     *
     * @return the currently selected interactive item
     */
    protected InteractiveItem currentInteractiveItem() {
        return interactiveItems.peekFirst();
    }

    /**
     * Retrieves the next item
     *
     * @return the next item in the deque
     */
    protected InteractiveItem nextInteractiveItem() {
        interactiveItems.addLast(interactiveItems.pollFirst());
        return interactiveItems.peekFirst();
    }

    /**
     * Retrieves the previous item
     *
     * @return the previous item in the deque
     */
    protected InteractiveItem previousInteractiveItem() {
        interactiveItems.addFirst(interactiveItems.pollLast());
        return interactiveItems.peekFirst();
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
        if (interactiveItems.size() > 0) {
            interactiveItems.forEach(InteractiveItem::show);
            currentInteractiveItem().onSelect();
        }
        if (nonInteractiveItems.size() > 0) {
            nonInteractiveItems.forEach(WindowItem::show);
        }
    }

    /**
     * Removes itself and eventually re-enables the console input
     */
    @Override
    public void remove() {
        super.remove();
        if (nonInteractiveItems.size() > 0)
            nonInteractiveItems.forEach(WindowItem::remove);
        if (interactiveItems.size() > 0)
            interactiveItems.forEach(InteractiveItem::remove);
        if (enableInputOnReturn)
            Console.in.enableConsoleInput();
        Console.closeWindow(this);
    }
}
