package it.polimi.ingsw.client.view.cli.console.graphics.components;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static it.polimi.ingsw.client.view.Constants.ANSI_RESET;

/**
 * Allows the use of graphics via ANSI arts, loading the corresponding files from the resources folder
 */
public class PrintableObject {
    private final int width;
    private final int height;
    private final String[][] object;

    /**
     * Loads the object from a text file
     * <p>
     * To work properly, each object has to be divided in a String matrix; each element of the matrix contains a
     * printable char and a special sequence (\033), followed by some parameters to color the char; the file must
     * not contain '\' characters other than the ones used for the escape code: when parsing the file, all the
     * backslashes are transcribed as '\003', and the "003" in the file is dumped, because it is not possible to
     * <i>read</i> an escape code; 'm' characters are also forbidden, as they mark that the end of the color sequence.
     * Note that the reset sequence is also stored in the matrix, so the caller has to take this into account.
     * <br>
     * In case those conditions are not satisfied, the method will not behave properly, but could end without
     * throwing exceptions: in case the object size is bigger than the provided size, all chars outside the matrix
     * will be ignored; if smaller, the elements of the matrix will be left to null.
     * <br>
     * For further information see <a href="https://stackoverflow.com/questions/4842424/list-of-ansi-color-escape-sequences">ANSI colors</a>
     *
     * @param file   the object file path
     * @param height the expected matrix height
     * @param width  the expected matrix width
     * @throws IOException if an I/O error occurs
     */
    public PrintableObject(InputStream file, int width, int height) throws IOException {

        if (file == null)
            throw new IOException("File not found");

        this.object = new String[height][width];
        this.width = width;
        this.height = height;

        int row = 0;
        int col = -1;
        int rawInput;
        char charRead;
        BufferedReader reader = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8));

        while ((rawInput = reader.read()) != -1) {

            charRead = (char) rawInput;
            if (charRead == '\n') {
                object[row][col] = ANSI_RESET;
                row++;
                col = -1;
                System.out.println();
            } else if (charRead == '\\') {
                col++;
                object[row][col] = "\033";
                reader.skip(3);
            } else
                object[row][col] += charRead;

        }
        reader.close();
    }

    public static PrintableObject load(Object caller, String filename, int width, int height) throws IOException {
        return new PrintableObject(caller.getClass().getResourceAsStream(filename), width, height);
    }

    public static PrintableObject load(String filePath, int width, int height) throws IOException {
        return new PrintableObject(new FileInputStream(filePath), width, height);
    }

    /**
     * <i>width</i> getter
     * <p>
     * Takes in account the ANSI reset code
     *
     * @return the number of columns in the matrix
     */
    public int getWidth() {
        return width;
    }

    /**
     * <i>height</i> getter
     *
     * @return the object height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the object, as a string matrix
     */
    public String[][] getObject() {
        return object;
    }
}
