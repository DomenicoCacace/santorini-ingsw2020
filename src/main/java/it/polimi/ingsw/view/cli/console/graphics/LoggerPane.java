package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.RawConsoleOutput;
import it.polimi.ingsw.view.cli.console.graphics.components.WindowItem;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class LoggerPane extends WindowItem {

    private final Deque<String> logMessages;
    private final String separator;

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
    public LoggerPane(WindowItem parent, CursorPosition initCoord, int width, int height, String ID) {
        super(parent, initCoord, width, height, ID);
        logMessages = new ArrayDeque<>();
        separator = getTextColor() + "----------------------";
    }

    public void addLogLine(String line, boolean urgent) {
        List<String> lines = RawConsoleOutput.splitMessage(line, width - 4);
        if (height - 2 < logMessages.size() + lines.size() + 1) { // messages on top must be removed
            int messagesToRemove = logMessages.size() + lines.size();
            for (int i = 0; i < messagesToRemove; i++) {
                lines.remove(0);
            }
        }
        if (urgent) {
            lines.forEach(l -> l = color.getLight() + l);
        }
        lines.forEach(logMessages::addLast);
        logMessages.addLast(separator);
        refresh();
    }

    /**
     * Draws black and white borders
     */
    @Override
    protected void drawBorders() {}

    private void refresh() {
        show();
        Console.cursor.setCoordinates(initCoord.getRow() + 1, initCoord.getCol() + 2);
        Console.cursor.moveCursorTo();
        Console.cursor.setAsHome();
        for (String s : logMessages) {
            Console.out.printlnAt(Console.cursor, s);
        }
    }
}
