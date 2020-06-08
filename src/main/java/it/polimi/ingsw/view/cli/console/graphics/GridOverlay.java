package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.*;
import it.polimi.ingsw.view.cli.console.prettyPrinters.FancyPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An invisible overlay to select the cells on the board
 */
public class GridOverlay extends ActiveItem implements Toggleable {

    private final FancyPrinter printer;
    private final List<PrintableObject> cellOverlays;   // 0: red; 1: blue; 2: yellow;
    private List<Cell> highlightedCells = new ArrayList<>();

    // coordinates range from 0 to 4
    private int coordX;
    private int coordY;

    public GridOverlay(Window parent, CursorPosition initCoord, FancyPrinter printer, String ID) throws IOException {
        super(parent, initCoord, ID);
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
     * Sets the cells to highlight, valid until this gets disabled
     *
     * @param highlightedCells the cells to highlight
     */
    public void setHighlightedCells(List<Cell> highlightedCells) {
        this.highlightedCells = highlightedCells;
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
        if (highlightedCells.stream().anyMatch(c -> c.getCoordX() == coordX && c.getCoordY() == coordY))
            printer.selectCell(new Cell(coordX, coordY), cellOverlays.get(0));  //FIXME: use another color
        else
            printer.deselectCell(new Cell(coordX, coordY));
    }

    /**
     * Defines the object behaviour when selected
     */
    @Override
    public void onSelect() {
        enable();
    }

    /**
     * Defines the object behaviour when released
     */
    @Override
    public void onRelease() {
        onDisable();
    }

    /**
     * Enables the component
     */
    @Override
    public void enable() {
        coordX = 2;
        coordY = 2;
        select();
    }

    /**
     * Disables the component
     */
    @Override
    public void onDisable() {
        ((Window) parent).getCli().evaluateInput(String.valueOf(coordY + 1));
        ((Window) parent).getCli().evaluateInput(String.valueOf(coordX + 1));
        this.highlightedCells = new ArrayList<>();
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

    /**
     * Returns the currently selected cell to the inputManager
     */
    @Override
    public void onCarriageReturn() {
        onDisable();
    }
}
