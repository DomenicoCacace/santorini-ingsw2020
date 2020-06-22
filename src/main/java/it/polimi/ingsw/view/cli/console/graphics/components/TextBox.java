package it.polimi.ingsw.view.cli.console.graphics.components;

import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;

/**
 * A field to be filled with text by the user
 */
public class TextBox extends ActiveItem {

    private final String label;
    private String inputText;

    /**
     * Default constructor
     * <p>
     * Creates a new textBox inside a window, using default properties
     *
     * @param parent    the textBox container
     * @param initCoord the textBox initial position relative to its container
     * @param label     the textBox label
     */
    public TextBox(InputDialog parent, CursorPosition initCoord, String label) {
        super(parent, initCoord, label);
        this.label = label;
        parent.getInputs().put(this, this.inputText);
    }

    /**
     * Custom constructor
     * <p>
     * Creates a custom sized textBox inside a window
     *
     * @param parent    the textBox container
     * @param initCoord the textBox initial position relative to its container
     * @param label     the textBox label
     * @param width     the label width
     * @param height    the label height
     */
    public TextBox(InputDialog parent, CursorPosition initCoord, int width, int height, String label) {
        super(parent, initCoord, width, height, label);
        this.label = label;
        parent.getInputs().put(this, this.inputText);
    }

    /**
     * Draws the textBox label
     */
    private void drawLabel() {
        Console.out.printAt(CursorPosition.offset(initCoord, -1, 2), label);
    }

    /**
     * Draws the textBox on the screen, drawing its background, borders and label
     */
    @Override
    public void show() {
        super.show();
        drawLabel();
    }

    /**
     * Shows the textBox shadows, placing the cursor at its beginning and removing its previous input
     */
    @Override
    public void onSelect() {
        Console.in.enableConsoleInput();
        Console.in.flushBuffer();
        show();
        drawShadows();
        Console.cursor.setCoordinates(initCoord.getRow() + 1, initCoord.getCol() + 1);
        Console.cursor.setAsHome();
        Console.cursor.moveToHome();
        Console.cursor.blink();
        inputText = "";
        ((InputDialog) parent).getInputs().replace(this, this.inputText);
    }

    /**
     * Hides the textBox shadows, saving an eventual input
     */
    @Override
    public void onRelease() {
        if (Console.in.currentInputSize() > 0) {
            inputText = Console.in.getCurrentBuffer();
            ((InputDialog) parent).getInputs().replace(this, this.inputText);
            Console.in.flushBuffer();
        }
        Console.cursor.stopBlink();
        hideShadows();
    }
}
