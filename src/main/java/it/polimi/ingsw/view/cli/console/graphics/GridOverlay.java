package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.PrintableObject;
import it.polimi.ingsw.view.cli.console.graphics.components.Toggleable;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;
import it.polimi.ingsw.view.cli.console.graphics.components.WindowItem;
import it.polimi.ingsw.view.cli.console.prettyPrinters.FancyPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An invisible overlay to select the cells on the board
 */
public class GridOverlay extends WindowItem implements Toggleable {

    private final FancyPrinter printer;
    private final List<PrintableObject> cellOverlays;   // 0: red; 1: blue; 2: yellow;

    // coordinates range from 0 to 4
    private int coordX;
    private int coordY;

    public GridOverlay(Window parent, CursorPosition initCoord, FancyPrinter printer) throws IOException {
        super(parent, initCoord);
        this.printer = printer;
        cellOverlays = new ArrayList<>();
        cellOverlays.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("cellRedBorder")),
                Integer.parseInt(properties.getProperty("cellWidth")), Integer.parseInt(properties.getProperty("cellHeight"))));
        /*cellOverlays.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("cellBlueBorder")),
                Integer.parseInt(properties.getProperty("cellWidth")), Integer.parseInt(properties.getProperty("cellHeight"))));*/
        /*cellOverlays.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("cellYellowBorder")),
                Integer.parseInt(properties.getProperty("cellWidth")), Integer.parseInt(properties.getProperty("cellHeight"))));*/
    }

    /**
     * Highlights the current cell
     */
    private void select() {
        printer.selectCell(new Cell(coordX, coordY), cellOverlays.get(0));
    }

    /**
     * Deselects the current cell
     */
    private void deselect() {
        printer.deselectCell(new Cell(coordX, coordY));
    }

    /**
     * Enables the component
     */
    @Override
    public void enable() {
        Console.in.addKeyEventListener(this);
        coordX = 2;
        coordY = 2;
        select();
    }

    /**
     * Disables the component
     */
    @Override
    public void onDisable() {
        parent.getCli().evaluateInput(String.valueOf(coordX + 1));
        parent.getCli().evaluateInput(String.valueOf(coordY + 1));
    }


    /**
     * Moves to the cell immediately above the current one, highlighting it;
     */
    @Override
    public void onArrowUp() {
        deselect();
        coordY = (5 + coordY - 1) % 5;
        select();
    }

    /**
     * Moves to the cell immediately below the current one, highlighting it;
     */
    @Override
    public void onArrowDown() {
        deselect();
        coordY = (coordY + 1) % 5;
        select();
    }

    /**
     * Moves to the cell immediately on the right of the current one, highlighting it;
     */
    @Override
    public void onArrowRight() {
        deselect();
        coordX = (coordX + 1) % 5;
        select();
    }

    /**
     * Moves to the cell immediately on the left of the current one, highlighting it;
     */
    @Override
    public void onArrowLeft() {
        deselect();
        coordX = (5 +coordX - 1) % 5;
        select();
    }
}
