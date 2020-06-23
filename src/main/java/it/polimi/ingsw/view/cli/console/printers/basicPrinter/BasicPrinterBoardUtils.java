package it.polimi.ingsw.view.cli.console.printers.basicPrinter;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.graphics.components.Window;
import it.polimi.ingsw.view.cli.console.printers.BoardUtils;

import java.io.IOException;
import java.util.List;

public class BasicPrinterBoardUtils extends BoardUtils {

    /**
     * Default constructor
     * <br>
     * Creates a new BoardUtils window, loading its settings from file
     *
     * @param parent the parent
     */
    public BasicPrinterBoardUtils(Window parent) throws IOException {
        super(parent, "Console");
    }

    @Override
    public void showGameBoard() {
        Console.out.drawMatrix(cachedBoard);
    }

    /**
     * Shows the current gameBoard on the screen, highlighting some cells
     *
     * @param toHighlight the cells to highlight
     */
    @Override
    public void showGameBoard(List<Cell> toHighlight) {
        String[][] gameBoard = cloneMatrix(cachedBoard);
        for (Cell cell : toHighlight) {
            highlight(cell, gameBoard);
        }
        Console.out.drawMatrix(gameBoard);
    }

    /**
     * Highlights the user's workers
     *
     * @param cells the cells containing the user's workers
     */
    @Override
    public void highlightWorkers(List<Cell> cells) {
        /*
         * Does nothing
         */
    }
}
