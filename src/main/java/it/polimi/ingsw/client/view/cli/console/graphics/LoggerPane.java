package it.polimi.ingsw.client.view.cli.console.graphics;

import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.CursorPosition;
import it.polimi.ingsw.client.view.cli.console.RawConsoleOutput;
import it.polimi.ingsw.client.view.cli.console.graphics.components.WindowItem;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
     *  @param parent    the Dialog containing this
     * @param initCoord the item coordinates
     * @param width     the object width
     * @param height    the object height
     */
    public LoggerPane(WindowItem parent, CursorPosition initCoord, int width, int height) {
        super(parent, initCoord, width, height);
        logMessages = new ArrayDeque<>();
        separator = getTextColor() + "----------------------";
    }

    /**
     * Adds a new line to the logger, eventually scrolling and deleting the previous ones
     *
     * @param line the line to add (can be split)
     * @param urgent alters the log text if true
     */
    public void addLogLine(String line, boolean urgent) {
        List<String> lines = RawConsoleOutput.splitMessage(line, width - 4);
        if (height - 2 < logMessages.size() + lines.size() + 1) { // messages on top must be removed
            int messagesToRemove = logMessages.size() - lines.size();
            if (messagesToRemove > 0) {
                for (int i = 0; i < lines.size(); i++)
                    logMessages.pop();
            }
        }
        if (urgent) {
            List<String> urgentMessage = new ArrayList<>();
            for (String s : lines)
                urgentMessage.add(color.getLight() + s);
            lines = urgentMessage;
        }
        lines.forEach(logMessages::addLast);
        logMessages.addLast(separator);
        refresh();
    }

    /**
     * Prints the log messages
     */
    private void refresh() {
        show();
        Console.cursor.setCoordinates(initCoord.getRow() + 1, initCoord.getCol() + 2);
        Console.cursor.moveCursorTo();
        Console.cursor.setAsHome();
        for (String s : logMessages) {
            Console.out.printlnAt(Console.cursor, s);
        }
    }

    /**
     * Does nothing, because the alternative color scheme is used to show urgent messages
     */
    @Override
    protected void drawBorders() {
        /*
         * Does nothing, because the alternative color scheme is used to show urgent messages
         */
    }
}
