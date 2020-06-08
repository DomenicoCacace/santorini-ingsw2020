package it.polimi.ingsw.view.cli.console;

import it.polimi.ingsw.view.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Raw console printing utilities
 */
public class RawConsoleOutput {

    /**
     * Splits a long message on multiple lines, separating the words at blank spaces OR whenever a '\n' is found
     *
     * @param message   the message to be split
     * @param maxLength the maximum length for a line
     * @return the original message, split in lines
     */
    public static List<String> splitMessage(String message, int maxLength) {
        List<String> adaptedLines = new ArrayList<>();
        List<String> lines = Arrays.stream(message.split("\\r?\\n")).collect(Collectors.toList());   // split lines when an end line character is found

        lines.forEach(line -> {
            char[] messageChars = line.toCharArray();
            int index = 0;
            int lastSpace = 0;
            while (index < line.length() - 1) {
                if (actualLength(line.substring(lastSpace)) <= maxLength) {
                    adaptedLines.add(line.substring(lastSpace));
                    break;
                }
                for (int i = Math.min(lastSpace + maxLength, line.length() - 1); i >= lastSpace; i--, index++) {
                    if (messageChars[i] == ' ') {
                        adaptedLines.add(line.substring(lastSpace, i));
                        lastSpace = i + 1;
                        break;
                    }
                }
            }
        });
        return adaptedLines;
    }

    /**
     * Provides the actual length of a string, excluding colors and other escape codes
     *
     * @param str the string to analyze
     * @return the number of <i>clean</i> characters
     */
    private static int actualLength(String str) {
        if (str == null)
            return 0;
        int len = 0;
        boolean esc = false;
        for (int pos = 0; pos < str.length(); pos++) {
            if (str.charAt(pos) == Constants.ESCAPE)
                esc = true;
            if (!esc)
                len++;
            if (esc && str.charAt(pos) == 'm')
                esc = false;
        }
        return len;
    }


    /**
     * Prints a string starting from the current cursor position
     * <br>
     * The cursor is moved at the end of the printed string
     *
     * @param str the string to print
     */
    public void print(String str) {
        printAt(Console.cursor, str);
    }

    /**
     * Prints a string starting from a certain position
     * <br>
     * The cursor is moved at the end of the printed string
     *
     * @param position the starting position
     * @param string   the string to print
     */
    public void printAt(CursorPosition position, String string) {
        System.out.print((String.format("\033[%d;%dH%s", position.getRow(), position.getCol(), string)));
        Console.placeCursor(position.getRow(), position.getCol() + actualLength(string));
    }

    /**
     * Prints a string starting from the current cursor position, then terminates the line
     * <br>
     * The cursor is moved at the beginning of the first free row from the starting point
     *
     * @param str the string to print
     */
    public void println(String str) {
        List<String> strings = splitMessage(str, Console.currentWindow().getWidth() - Console.cursor.getCol() - 2);
        for (String string : strings)
            System.out.print(String.format("%s", /*Console.currentWindow().getColor() +*/ string));
        Console.placeCursor(Console.cursor.getRow() + strings.size(), 0);
        //Console.cursor.moveCursorTo();
    }

    /**
     * Prints a string starting from a certain position, then terminates the line
     * <br>
     * The cursor is moved one row below the initial cursor position
     *
     * @param position the starting position
     * @param string   the string to print
     */
    public void printlnAt(CursorPosition position, String string) {
        System.out.print((String.format("\033[%d;%dH%s\n", position.getRow(), position.getCol(), string)));
        Console.placeCursor(position.getRow() + 1, position.getCol());
    }

    /**
     * Shows a matrix on the console
     *
     * @param matrix    the matrix to show
     * @param initCoord the matrix initial position
     */
    public void drawMatrix(String[][] matrix, CursorPosition initCoord) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++)
                Console.out.printAt(CursorPosition.offset(initCoord, row, col), matrix[row][col]);
        }
    }

    /**
     * Shows a matrix on the console
     *
     * @param matrix    the matrix to show
     */
    public void drawMatrix(String[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++)
                Console.out.print(matrix[row][col]);
        }
    }

    /**
     * Echoes back on the console the pressed key
     *
     * @param c the pressed key
     */
    protected void echo(char c) {
        System.out.print(String.format("%s%c", Console.currentWindow().getTextColor(), c));
    }

    /**
     * Clears the last character printed
     */
    protected void del() {
        System.out.print(Constants.CURSOR_BACK + Console.currentWindow().getTextColor() + " " + Constants.CURSOR_BACK);
    }
}

