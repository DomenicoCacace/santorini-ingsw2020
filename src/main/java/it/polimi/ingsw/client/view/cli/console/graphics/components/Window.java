package it.polimi.ingsw.client.view.cli.console.graphics.components;

import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.CursorPosition;
import it.polimi.ingsw.client.view.cli.console.KeyEventListener;

import static it.polimi.ingsw.client.view.Constants.ANSI_RESET;

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
        initCoord.setCoordinates(getInitCoord().getRow() + (Console.numberOfWindowsOpen() - 1) * 2, getInitCoord().getCol() + (Console.numberOfWindowsOpen() - 1) * 2);
        background = setBackground(color.getDark() + " ");
    }

    /**
     * Custom constructor
     * <br>
     * Creates a custom sized window
     *
     * @param parent    the caller
     * @param width     the window width
     * @param height    the window height
     * @param initCoord the window initial coordinates
     */
    public Window(Window parent, int width, int height, CursorPosition initCoord) {
        super(parent, initCoord, width, height);
        this.cli = parent.cli;
        enableInputOnReturn = Console.in.isConsoleInputEnabled();
        returnTo = CursorPosition.offset(Console.cursor, -1, 0);
        background = setBackground(color.getDark() + " ");
    }

    /**
     * <i>cli</i> getter
     *
     * @return the cli containing this
     */
    @Override
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
     * Sets the background as a uniform matrix
     *
     * @param defaultString the default string (color + " ")
     * @return the window background
     */
    private String[][] setBackground(String defaultString) {
        Console.cursor.setCoordinates(0, 0);
        Console.cursor.moveCursorTo();
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
        Console.cursor.setCoordinates(0, 0);
        Console.cursor.moveCursorTo();
        String[][] object = obj.getObject();
        for (int row = coord.getRow(); row < Math.min(obj.getHeight() + coord.getRow(), height); row++) {
            for (int col = coord.getCol(); col < Math.min(obj.getWidth() + coord.getCol(), width); col++) {
                String val = object[row - coord.getRow()][col - coord.getCol()];
                background[row][col] = val;
                if (val.contains(ANSI_RESET)) {
                    background[row][col] = getBackgroundColor() + " ";
                }
            }
        }
    }

    /**
     * Shows the window on the console
     *
     */
    @Override
    public void show() {
        Console.out.drawMatrix(background, initCoord);
        Console.addWindow(this);
    }
}
