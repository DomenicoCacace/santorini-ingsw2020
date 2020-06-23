package it.polimi.ingsw.view.cli.console.graphics;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.graphics.components.ActiveItem;
import it.polimi.ingsw.view.cli.console.graphics.components.PrintableObject;
import it.polimi.ingsw.view.cli.console.graphics.components.Toggleable;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;
import it.polimi.ingsw.view.cli.console.printers.fancyPrinter.FancyPrinterBoardUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An invisible overlay to select the cells on the board
 */
public final class GridOverlay extends ActiveItem implements Toggleable {

    private final FancyPrinterBoardUtils printer;
    private final List<PrintableObject> cellOverlays;   // 0: red 1: blue 2: yellow
    private List<Cell> highlightedCells = new ArrayList<>();

    // coordinates range from 0 to 4
    private int coordX;
    private int coordY;

    private int defaultCellFrame;

    public GridOverlay(Window parent, CursorPosition initCoord, FancyPrinterBoardUtils printer, String id) throws IOException {
        super(parent, initCoord, id);
        this.printer = printer;
        cellOverlays = new ArrayList<>();
        int cellWidth = Integer.parseInt(properties.getProperty("cellWidth"));
        int cellHeight = Integer.parseInt(properties.getProperty("cellHeight"));
        cellOverlays.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty(
                "cellRedBorder")), cellWidth, cellHeight));
        cellOverlays.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("cellBlueBorder")),
                cellWidth, cellHeight));
        cellOverlays.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("cellYellowBorder")),
                cellWidth, cellHeight));
    }


    /**
     * Sets the cells to highlight, valid until this gets disabled
     *
     * @param highlightedCells the cells to highlight
     */
    public void setHighlightedCells(List<Cell> highlightedCells) {
        this.highlightedCells = highlightedCells;
        if (!highlightedCells.isEmpty())
            defaultCellFrame = 0;
        else
            defaultCellFrame = 1;
    }

    /**
     * Highlights the current cell
     */
    private void select() {
        if (highlightedCells.stream().anyMatch(c -> c.getCoordX() == coordY && c.getCoordY() == coordX))
            printer.selectCell(new Cell(coordX, coordY), cellOverlays.get(1));
        else
            printer.selectCell(new Cell(coordX, coordY), cellOverlays.get(defaultCellFrame));
    }

    /**
     * Deselects the current cell
     */
    private void deselect() {
        if (highlightedCells.stream().anyMatch(c -> c.getCoordX() == coordY && c.getCoordY() == coordX))
            printer.selectCell(new Cell(coordX, coordY), cellOverlays.get(2));
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
    }

    /**
     * Disables the component
     */
    @Override
    public void onDisable() {
        deselect();
        highlightedCells.forEach(c -> printer.deselectCell(new Cell(c.getCoordX(), c.getCoordY())));
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

    /**
     * (Does not) print the object on the screen
     */
    @Override
    public void show() {
        /*
         * This item is supposed to be invisible, so its show() method is empty
         */
    }
}
