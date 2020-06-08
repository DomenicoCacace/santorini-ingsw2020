package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static it.polimi.ingsw.view.Constants.*;

/**
 * An abstraction for any kind of item that can be put inside a window, including a window itself
 */
public abstract class WindowItem extends Rectangle {

    /**
     * The window which invoked the dialog
     */
    protected final WindowItem parent;

    /**
     * The item ID
     */
    protected final String ID;

    /**
     * A list containing all the interactive items
     */
    protected final Deque<ActiveItem> activeItems;

    /**
     * A list containing all non-interactive items (e.g. {@linkplain DetailPane})
     */
    protected final List<WindowItem> passiveItems;

    /**
     * Window constructor
     * <p>
     * Creates a Window using its default settings
     * </p>
     *
     * @param parent the Window containing this
     * @param ID the object ID
     */
    protected WindowItem(Window parent, String ID) {
        super();
        this.parent = parent;
        this.ID = ID;
        this.activeItems = new ArrayDeque<>();
        this.passiveItems = new ArrayList<>();
    }

    /**
     * Default constructor
     * <p>
     * Creates an item using its default settings;
     * <br>
     * The initial coordinates are relative to the object's parent
     *
     * @param parent    the Window containing this
     * @param initCoord the item coordinates
     * @param ID the object ID
     */
    protected WindowItem(WindowItem parent, CursorPosition initCoord, String ID) {
        this.parent = parent;
        this.initCoord.setCoordinates(initCoord.getRow() + parent.getInitCoord().getRow(),
                initCoord.getCol() + parent.getInitCoord().getCol());
        this.ID = ID;
        this.activeItems = new ArrayDeque<>();
        this.passiveItems = new ArrayList<>();
    }

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
     * @param ID  the object ID
     */
    protected WindowItem(WindowItem parent, CursorPosition initCoord, int width, int height, String ID) {
        super(initCoord, width, height);
        this.parent = parent;
        this.initCoord.setCoordinates(initCoord.getRow() + parent.getInitCoord().getRow(),
                initCoord.getCol() + parent.getInitCoord().getCol());
        this.ID = ID;
        this.activeItems = new ArrayDeque<>();
        this.passiveItems = new ArrayList<>();
    }

    /**
     * Adds a new interactive item to the items list
     *
     * @param item the item to add
     */
    protected void addInteractiveItem(ActiveItem item) {
        activeItems.addLast(item);
    }

    /**
     * Adds a new item to the items list
     *
     * @param item the item to add
     */
    protected void addNonInteractiveItem(WindowItem item) {
        passiveItems.add(item);
    }

    /**
     * Provides the dialog item which is currently selected
     *
     * @return the currently selected interactive item
     */
    protected ActiveItem currentInteractiveItem() {
        return activeItems.peekFirst();
    }

    /**
     * Retrieves the next item
     *
     * @return the next item in the deque
     */
    protected ActiveItem nextInteractiveItem() {
        activeItems.addLast(activeItems.pollFirst());
        return activeItems.peekFirst();
    }

    /**
     * Retrieves the previous item
     *
     * @return the previous item in the deque
     */
    protected ActiveItem previousInteractiveItem() {
        activeItems.addFirst(activeItems.pollLast());
        return activeItems.peekFirst();
    }

    /**
     * Provides the length of the longest string in a list
     *
     * @param strings the list of strings
     * @return the length of the longest string
     */
    public static int maxStringLength(List<String> strings) {
        return strings.stream().mapToInt(String::length).filter(s -> s >= 0).max().orElse(0);
    }

    /**
     * <i>parent</i> getter
     *
     * @return this item's parent
     */
    public WindowItem getParent() {
        return parent;
    }

    /**
     * <i>ID</i> getter
     *
     * @return the WindowItem ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Draws right and bottom shadows
     * <br>
     * Uses the parent color scheme, to merge seamlessly with the background
     */
    @Override
    protected void drawShadows() {
        for (int row = 1; row < height - 1; row++)
            Console.out.printAt(CursorPosition.offset(initCoord, row, width), parent.getColor().getBackgroundDark() + LINE_SHADOW_RIGHT);

        for (int col = 1; col < width; col++)
            Console.out.printAt(CursorPosition.offset(initCoord, height, col), parent.getColor().getBackgroundDark() + LINE_SHADOW_BOTTOM);

        Console.out.printAt(CursorPosition.offset(initCoord, height, 0), parent.getColor().getLight());
        Console.out.printAt(CursorPosition.offset(initCoord, height, width), parent.getColor().getBackgroundDark() + LINE_SHADOW_CORNER);
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width), parent.getColor().getBackgroundDark() + LINE_SHADOW_RIGHT);
    }

    /**
     * Hides the shadows
     */
    @Override
    protected void hideShadows() {
        for (int row = 1; row < height - 1; row++)
            Console.out.printAt(CursorPosition.offset(initCoord, row, width), parent.getColor().getBackgroundDark() + " ");

        for (int col = 1; col < width; col++)
            Console.out.printAt(CursorPosition.offset(initCoord, height, col), parent.getColor().getBackgroundDark() + " ");

        Console.out.printAt(CursorPosition.offset(initCoord, height, 0), parent.getColor().getLight());
        Console.out.printAt(CursorPosition.offset(initCoord, height, width), parent.getColor().getBackgroundDark() + " ");
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width), parent.getColor().getBackgroundDark() + " ");
    }

    /**
     * Prints the object on the screen
     */
    public void show() {
        drawBackground();
        drawBorders();
    }

    /**
     * Removes itself, restoring the underlying dialogs
     */
    public void remove() {
        try {
            Window caller = ((Window) this.parent);
            int row;
            int col = initCoord.getCol();
            do {

                for (row = initCoord.getRow(); row < initCoord.getRow() + Math.min(caller.height - initCoord.getRow(), height + 1); row++) {
                    for (col = initCoord.getCol(); col < initCoord.getCol() + Math.min(caller.width - initCoord.getCol(), width + 1); col++)
                        Console.out.printAt(new CursorPosition(row, col), caller.background[row][col]);
                }
                if (caller.parent != null)
                    caller = ((Window) caller.parent);
                else
                    break;
            } while (row < initCoord.getRow() + height && col < initCoord.getCol() + width);
        }
        catch (ClassCastException e) {
            System.exit(-1);
        }
    }
}
