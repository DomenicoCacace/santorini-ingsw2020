package it.polimi.ingsw.client.view.cli.console.graphics.components;

import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.CursorPosition;

import java.util.Properties;

import static it.polimi.ingsw.client.view.Constants.*;

/**
 * More than just a rectangle
 */
public abstract class Rectangle {

    /**
     * The rectangle height
     */
    protected final int height;

    /**
     * The rectangle width
     */
    protected final int width;

    /**
     * The rectangle color scheme
     */
    protected final Color color;

    /**
     * The position to start drawing the rectangle from
     */
    protected final CursorPosition initCoord = new CursorPosition();

    /**
     * The object properties
     */
    protected final Properties properties = new Properties();

    /**
     * Default constructor
     * <br>
     * Creates a new Rectangle object, loading the settings from a file
     */
    protected Rectangle() {
        loadPropertiesFile();
        color = new Color(this.getClass().getResourceAsStream(this.getClass().getSimpleName() + ".xml"));
        height = Integer.parseInt(properties.getProperty("height", "63"));
        width = Integer.parseInt(properties.getProperty("width", "190"));
        initCoord.setCoordinates(Integer.parseInt(properties.getProperty("startRow", "0")),
                Integer.parseInt(properties.getProperty("startCol", "0")));
    }

    /**
     * Custom constructor
     * <br>
     * Creates a new Rectangle object, using the given parameters
     *
     * @param width     the object width
     * @param height    the object height
     * @param initCoord the object initial coordinates
     */
    protected Rectangle(CursorPosition initCoord, int width, int height) {
        loadPropertiesFile();
        color = new Color(this.getClass().getResourceAsStream(this.getClass().getSimpleName() + ".xml"));
        this.width = width;
        this.height = height;
        this.initCoord.setCoordinates(initCoord.getRow(), initCoord.getCol());
    }

    /**
     * Finds the index to start from to center an item into a container
     *
     * @param containerSize the container size
     * @param itemSize      the item size
     * @return the coordinate to center the item in the container
     */
    protected static int findCenter(int containerSize, int itemSize) {
        return (containerSize - itemSize) / 2;
    }

    /**
     * Loads the properties file
     */
    protected void loadPropertiesFile() {
        try {
            properties.loadFromXML(this.getClass().getResourceAsStream(this.getClass().getSimpleName() + ".xml"));
        } catch (Exception e) {
            System.out.println("Missing properties file for " + this.getClass().getSimpleName());
        }
    }

    /**
     * <i>height</i> getter
     *
     * @return the rectangle height
     */
    public int getHeight() {
        return height;
    }

    /**
     * <i>width</i> getter
     *
     * @return the rectangle width
     */
    public int getWidth() {
        return width;
    }

    /**
     * <i>color</i> getter
     *
     * @return the rectangle colors config
     */
    public Color getColor() {
        return color;
    }

    /**
     * <i>initCoord</i> getter
     *
     * @return the rectangle initial coordinates
     */
    public CursorPosition getInitCoord() {
        return initCoord;
    }

    /**
     * <i>backgroundColor</i> getter
     *
     * @return the rectangle dark color scheme
     */
    public String getBackgroundColor() {
        return color.dark;
    }

    /**
     * <i>textColor</i> getter
     *
     * @return the color of the text to be printed on this
     */
    public String getTextColor() {
        return color.dark;
    }

    /**
     * Draws the rectangle on the screen
     */
    protected void drawBackground() {
        String line = (color.dark + " ").repeat(width);
        int row;
        for (row = 0; row < height; row++)
            Console.out.printAt(CursorPosition.offset(initCoord, row, 0), line);
    }

    /**
     * Draws black and white borders
     */
    protected void drawBorders() {

        // VERTICAL LINES
        for (int row = 0; row < height - 1; row++) {
            Console.out.printAt(CursorPosition.offset(initCoord, row, 0), color.light + LINE_VERTICAL);
            Console.out.printAt(CursorPosition.offset(initCoord, row, width - 1), color.dark + LINE_VERTICAL);
        }
        Console.out.printAt(CursorPosition.offset(initCoord, 0, width), color.light);

        // CORNERS
        Console.out.printAt(initCoord, color.light + LINE_TOP_LEFT);
        Console.out.printAt(CursorPosition.offset(initCoord, 0, width - 1), color.dark + LINE_TOP_RIGHT);
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, 0), color.light + LINE_BOTTOM_LEFT);
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width - 1), color.dark + LINE_BOTTOM_RIGHT);

        // HORIZONTAL LINES
        for (int col = 1; col < width - 1; col++) {
            Console.out.printAt(CursorPosition.offset(initCoord, 0, col), color.light + LINE_HORIZONTAL);
            Console.out.printAt(CursorPosition.offset(initCoord, height - 1, col), color.dark + LINE_HORIZONTAL);
        }
    }

    /**
     * Hides the borders
     */
    protected void hideBorders() {
        // VERTICAL LINES
        for (int row = 0; row < height - 1; row++) {
            Console.out.printAt(CursorPosition.offset(initCoord, row, 0), color.light + " ");
            Console.out.printAt(CursorPosition.offset(initCoord, row, width - 1), color.dark + " ");
        }
        Console.out.printAt(CursorPosition.offset(initCoord, 0, width), color.light);

        // CORNERS
        Console.out.printAt(initCoord, color.light + " ");
        Console.out.printAt(CursorPosition.offset(initCoord, 0, width - 1), color.dark + " ");
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, 0), color.light + " ");
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width - 1), color.dark + " ");

        // HORIZONTAL LINES
        for (int col = 1; col < width - 1; col++) {
            Console.out.printAt(CursorPosition.offset(initCoord, 0, col), color.light + " ");
            Console.out.printAt(CursorPosition.offset(initCoord, height - 1, col), color.dark + " ");
        }
    }

    /**
     * Draws right and bottom shadows
     */
    protected void drawShadows() {
        for (int row = 1; row < height - 1; row++)
            Console.out.printAt(CursorPosition.offset(initCoord, row, width), color.backgroundDark + LINE_SHADOW_RIGHT);

        for (int col = 1; col < width; col++)
            Console.out.printAt(CursorPosition.offset(initCoord, height, col), color.backgroundDark + LINE_SHADOW_BOTTOM);

        Console.out.printAt(CursorPosition.offset(initCoord, height, 0), color.light);
        Console.out.printAt(CursorPosition.offset(initCoord, height, width), color.backgroundDark + LINE_SHADOW_CORNER);
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width), color.backgroundDark + LINE_SHADOW_RIGHT);
    }

    /**
     * Hides the shadows
     */
    protected void hideShadows() {
        for (int row = 1; row < height - 1; row++)
            Console.out.printAt(CursorPosition.offset(initCoord, row, width), color.backgroundDark + " ");

        for (int col = 1; col < width; col++)
            Console.out.printAt(CursorPosition.offset(initCoord, height, col), color.backgroundDark + " ");

        Console.out.printAt(CursorPosition.offset(initCoord, height, 0), color.light);
        Console.out.printAt(CursorPosition.offset(initCoord, height, width), color.backgroundDark + " ");
        Console.out.printAt(CursorPosition.offset(initCoord, height - 1, width), color.backgroundDark + " ");
    }
}
