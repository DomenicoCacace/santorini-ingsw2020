package it.polimi.ingsw.view.cli.console;

import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;

import java.io.IOException;
import java.util.Stack;

/**
 * Console utilities
 */
public class Console extends Window implements KeyEventListener {
    public static final RawConsoleInput in = new RawConsoleInput(80);
    public static final RawConsoleOutput out = new RawConsoleOutput();
    public static final CursorPosition cursor = new CursorPosition();    //fixme make private
    private static final String[] rawMode = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
    private static final String[] saneMode = {"/bin/sh", "-c", "stty sane echo </dev/tty"};
    public static final Stack<Window> windowsOpen = new Stack<>();

    /**
     * Default constructor
     * <p>
     * Determines if the terminal supports advanced features and, if available, enables them
     * </p>
     */
    private Console(CLI cli) {
        //TODO: manage non compatible stuff
        super(cli);
        enableRawMode();
        in.addKeyEventListener(cursor);
        new Thread(in::listen).start();
        Console.in.enableConsoleInput();
    }

    /**
     * Initializes the Console
     *
     * @param cli the view invoking the console
     */
    public static Console init(CLI cli) {
        windowsOpen.forEach(Window::remove);
        windowsOpen.removeAllElements();
        Console console = new Console(cli);
        addWindow(console);
        return console;
    }

    /**
     * Provides the last window opened
     *
     * @return the last window opened, null if none has been shown
     */
    public static Window currentWindow() {
        try {
            return windowsOpen.peek();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Provides the number of windows open
     *
     * @return the number of open windows
     */
    public static int windowsOpen() {
        return windowsOpen.size();
    }

    /**
     * Adds, if absent, a new Window to the console windows list
     *
     * @param window the window to add
     */
    public static void addWindow(Window window) {
        if (!windowsOpen.contains(window)) {
            in.removeKeyEventListener(currentWindow());
            windowsOpen.push(window);
            in.addKeyEventListener(window);
        }
    }

    /**
     * Closes the given window, re-enabling the underlying one
     *
     * @param window the window to close
     */
    public static void closeWindow(Window window) {
        in.removeKeyEventListener(window);
        windowsOpen.remove(window);
        if (currentWindow().getParent() != null)  // a dialog, not the console
            windowsOpen.peek().show();
        cursor.setCoordinates(window.getReturnTo().getRow(), window.getReturnTo().getCol());
        cursor.moveCursorTo();
        in.addKeyEventListener(windowsOpen.peek());
    }

    /**
     * Closes the program, bringing the terminal back to canonical mode
     */
    public static void close() {
        disableRawMode();
        System.exit(0);
    }

    /**
     * Places the cursor at the given coordinates
     *
     * @param row the target row
     * @param col the target col
     */
    public static void placeCursor(int row, int col) {
        cursor.setCoordinates(row, col);
        cursor.moveCursorTo();
    }

    /**
     * Makes the cursor blink
     */
    public static void showCursor() {
        cursor.blink();
    }

    /**
     * Disables raw mode
     * <p>
     * Raw mode will have effects on the console even after the program shutdown, so this method has to be called
     * before closing the program.
     * </p>
     */
    private static void disableRawMode() {
        try {
            Runtime.getRuntime().exec(saneMode).waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables raw mode
     * <p>
     * Enabling raw mode, the console echo is disabled for all characters
     */
    private void enableRawMode() {
        try {
            Runtime.getRuntime().exec(rawMode).waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


}
