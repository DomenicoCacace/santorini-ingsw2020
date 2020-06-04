package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.KeyEventListener;

import static it.polimi.ingsw.view.Constants.ANSI_RESET;

/**
 * General abstraction for a window
 */
public abstract class Window extends WindowItem implements KeyEventListener {


    /**
     * Determines if the console input has to be enabled when closing this
     */
    protected final boolean enableInputOnReturn;
    /**
     * The cursor position to restore on window exit
     */
    protected final CursorPosition returnTo;

    /**
     * The View object
     */
    protected final CLI cli;


    /**
     * The window background
     */
    protected final String[][] background;


    /**
     * Console constructor
     * <br>
     * Creates the initial window, centered
     */
    public Window(CLI cli) {
        super(null);
        this.cli = cli;
        enableInputOnReturn = false;
        returnTo = new CursorPosition();
        background = setBackground(color.getDark() + " ");
    }


    /**
     * Default constructor
     * <br>
     * Creates a new Window, loading its settings from file
     *
     * @param parent the parent
     */
    public Window(Window parent) {
        super(parent);
        this.cli = parent.cli;
        enableInputOnReturn = Console.in.isConsoleInputEnabled();
        returnTo = CursorPosition.offset(Console.cursor, 0, 0);
        initCoord.setCoordinates(getInitCoord().getRow() + (Console.windowsOpen() - 1) * 2, getInitCoord().getCol() + (Console.windowsOpen() - 1) * 2);
        background = setBackground(color.getDark() + " ");
    }

    /**
     * Custom constructor
     * <br>
     * Creates a custom sized window
     *
     * @param width     the window width
     * @param height    the window height
     * @param initCoord the window initial coordinates
     * @param parent    the caller
     */
    public Window(Window parent, int width, int height, CursorPosition initCoord) {
        super(parent, initCoord, width, height);
        this.cli = parent.cli;
        enableInputOnReturn = Console.in.isConsoleInputEnabled();
        returnTo = CursorPosition.offset(Console.cursor, -1, 0);
        background = setBackground(color.getDark() + " ");
    }

    /**
     * <i>width</i> getter
     *
     * @return the window width
     */
    public int getWidth() {
        return width;
    }

    /**
     * <i>height</i> getter
     *
     * @return the windows height
     */
    public int getHeight() {
        return height;
    }

    /**
     * <i>cli</i> getter
     *
     * @return the cli containing this
     */
    public CLI getCli() {
        return cli;
    }

    /**
     * <i>returnTo</i> getter
     *
     * @return the cursor position to restore when closing the window
     */
    public CursorPosition getReturnTo() {
        return returnTo;
    }

    /**
     * <i>caller</i> getter
     *
     * @return the Window which opened this
     */
    public Window getParent() {
        return parent;
    }

    /**
     * Sets the background as a uniform matrix
     *
     * @param defaultString the default string (color + " ")
     * @return the window background
     */
    private String[][] setBackground(String defaultString) {
        String[][] newScene = new String[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                newScene[row][col] = defaultString;
            }
            newScene[row][width - 1] = ANSI_RESET;
        }
        return newScene;
    }

    /**
     * Adds a PrintableObject to the background
     *
     * @param obj   the printableObject to add
     * @param coord the starting coordinates of the scene
     */
    public void addToBackground(PrintableObject obj, CursorPosition coord) {
        String[][] object = obj.getObject();
        for (int row = coord.getRow(); row < Math.min(obj.getHeight() + coord.getRow(), height); row++) {
            for (int col = coord.getCol(); col < Math.min(obj.getWidth() + coord.getCol(), width); col++)
                background[row][col] = object[row - coord.getRow()][col - coord.getCol()];
        }
    }

    /**
     * Shows the window on the console
     */
    public void show() {
        Console.out.drawMatrix(background, initCoord);
        Console.addWindow(this);
    }
}
