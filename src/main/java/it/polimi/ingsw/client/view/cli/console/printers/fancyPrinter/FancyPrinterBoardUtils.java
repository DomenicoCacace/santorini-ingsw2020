package it.polimi.ingsw.client.view.cli.console.printers.fancyPrinter;

import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.CursorPosition;
import it.polimi.ingsw.client.view.cli.console.KeyEventListener;
import it.polimi.ingsw.client.view.cli.console.graphics.GridOverlay;
import it.polimi.ingsw.client.view.cli.console.graphics.ListPane;
import it.polimi.ingsw.client.view.cli.console.graphics.LoggerPane;
import it.polimi.ingsw.client.view.cli.console.graphics.SingleChoiceListPane;
import it.polimi.ingsw.client.view.cli.console.graphics.components.PrintableObject;
import it.polimi.ingsw.client.view.cli.console.graphics.components.WindowItem;
import it.polimi.ingsw.client.view.cli.console.printers.BoardUtils;
import it.polimi.ingsw.shared.dataClasses.Block;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.PlayerData;
import it.polimi.ingsw.shared.dataClasses.PossibleActions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection of WindowItems and utility methods to decorate the game board, giving information about the game status
 */
public class FancyPrinterBoardUtils extends BoardUtils implements KeyEventListener {

    private final CursorPosition boardOffset;

    private final FancyPrinter printer;
    private GridOverlay gridOverlay;
    private ListPane playerInfo;
    private LoggerPane messages;
    private SingleChoiceListPane selector;



    public FancyPrinterBoardUtils(FancyPrinter printer) throws IOException {
        super(printer.getConsole());
        this.printer = printer;
        Console.in.disableConsoleInput();
        this.messages = new LoggerPane(this, new CursorPosition(2,124), 64, 25);
        this.initCoord.setCoordinates(0, 0);
        this.boardOffset = new CursorPosition(1, 1);
        this.addToBackground(new PrintableObject(printer.getClass().getResourceAsStream(properties.getProperty("emptyBoardPath")), boardWidth, boardHeight), boardOffset);
        gridOverlay = new GridOverlay(printer.getConsole(), boardOffset, this);
        passiveItems.add(gridOverlay);
    }

    /**
     * Removes itself, restoring the underlying dialogs
     */
    @Override
    public void remove() {
        super.remove();
        messages = new LoggerPane(this, new CursorPosition(2,124), 64, 25);
        playerData = null;
        try {
            gridOverlay = new GridOverlay(printer.getConsole(), boardOffset, this);
        }catch (IOException e) {
            System.err.println("Error");
        }
    }

    /**
     * Shows the window on the console
     */
    @Override
    public void show() {
        super.show();
        activeItems.forEach(WindowItem::show);
        messages.show();
    }

    public void enableGridInput() {
        activeItems.addFirst(gridOverlay);
        gridOverlay.enable();
    }

    /**
     * Enables the user to select a cell, using a {@linkplain GridOverlay}
     */
    public void enableGridInput(List<Cell> highlighted) {
        enableGridInput();
        gridOverlay.setHighlightedCells(highlighted);
    }

    public void setPlayerData(List<PlayerData> playerData) {
        if (playerData != null) {
            Map<String, List<String>> players = new HashMap<>();
            playerData.forEach(p -> {
                List<String> description = new ArrayList<>();
                description.add("---" + p.getName() + "---");
                description.add("Color: " + p.getColor().toString());
                description.add("God: " + p.getGod().getName() + "(" + p.getGod().getName() + " workers)");
                description.add("");
                description.add("-- Effect description -- ");
                description.add(p.getGod().getDescriptionStrategy());
                players.put(p.getName(), description);
            });

            playerInfo = new ListPane(this, new CursorPosition(41, 124), 64, 20, players);
            playerInfo.show();
            activeItems.addLast(playerInfo);
        }
    }

    /**
     * Shows a dialog asking the user to select an action to perform
     *
     * @param actions a list of possible actions
     */
    public void enableActionSelector(List<PossibleActions> actions) {
        List<String> options = new ArrayList<>();
        actions.forEach(a -> options.add(a.toString()));
        selector = new SingleChoiceListPane(this, new CursorPosition(29, 124), 64, 10, options);
        activeItems.addFirst(selector);
        selector.show();
    }

    public void enableBlockSelector(List<Block> buildableBlocks) {
        List<String> options = new ArrayList<>();
        buildableBlocks.forEach(b -> options.add(b.name()));
        selector = new SingleChoiceListPane(this, new CursorPosition(29, 124), 64, 10, options);
        activeItems.addFirst(selector);
        selector.show();
    }

    public void showErrorMessage(String error) {
        messages.addLogLine(error, true);
    }

    public void showMessage(String message) {
        messages.addLogLine(message, false);
    }

    public void showGameBoard() {
        Console.out.drawMatrix(cachedBoard, boardOffset);
        activeItems.forEach(WindowItem::show);
    }

    public void showGameBoard(List<Cell> toHighlight) {
        String[][] tempBoard = cloneMatrix(cachedBoard);
        for (Cell cell : toHighlight)
            highlight(cell, tempBoard);
        Console.out.drawMatrix(tempBoard, boardOffset);
    }

    /**
     * Highlights the user's workers
     *
     * @param cells the cells containing the user's workers
     */
    @Override
    public void highlightWorkers(List<Cell> cells) {
        showGameBoard(cells);
    }

    /**
     * Highlights a single cell
     *
     * @param cell the cell to highlight
     * @param cellBorder the PrintableObject containing the cell border
     */
    public void selectCell(Cell cell, PrintableObject cellBorder) {
        int colOffset = verticalWallWidth + (cellWidth+verticalWallWidth)*cell.getCoordX();
        int rowOffset = horizontalWallWidth + (cellHeight+horizontalWallWidth)*cell.getCoordY();
        CursorPosition cellOffset = CursorPosition.offset(boardOffset, rowOffset, colOffset);

        String[][] cellToHighlight = subMatrix(cachedBoard, rowOffset, colOffset, cellHeight, cellWidth);

        for (int row = 0; row < cellHeight; row++) {
            for (int col = 0; col < cellWidth; col++) {
                if (!cellBorder.getObject()[row][col].contains("\033[0m"))  // ignores resets to preserve the original background
                    cellToHighlight[row][col] = cellBorder.getObject()[row][col];
            }
        }
        Console.out.drawMatrix(cellToHighlight, cellOffset);
    }

    /**
     * Deselects a cell, hiding the highlighted border
     *
     * @param cell the cell to disable
     */
    public void deselectCell(Cell cell) {
        //inverted heere
        int colOffset = verticalWallWidth + (cellWidth+verticalWallWidth)*cell.getCoordX();
        int rowOffset = horizontalWallWidth + (cellHeight+horizontalWallWidth)*cell.getCoordY();
        CursorPosition cellOffset = CursorPosition.offset(boardOffset, rowOffset, colOffset);

        String[][] cellToHighlight = subMatrix(cachedBoard, rowOffset, colOffset, cellHeight, cellWidth);
        Console.out.drawMatrix(cellToHighlight, cellOffset);
    }

    /**
     * Defines the listener behavior when the <code>Enter</code> key is pressed
     */
    @Override
    public void onCarriageReturn() {
        currentActiveItem().onCarriageReturn();
        if (currentActiveItem().equals(gridOverlay)) {
            activeItems.remove(gridOverlay);
        }
        if (selector != null) {
            selector.remove();
            activeItems.remove(selector);
            selector = null;
        }
    }

    /**
     * Defines the listener behavior when the <code>Tab</code> key is pressed
     */
    @Override
    public void onTab() {
        if (playerInfo != null)
            playerInfo.onArrowRight();
    }

    /**
     * Defines the listener behavior when the <code>Arrow Up</code> key is pressed
     */
    @Override
    public void onArrowUp() {
        if (currentActiveItem() != null)
            currentActiveItem().onArrowUp();

    }

    /**
     * Defines the listener behavior when the <code>Arrow Down</code> key is pressed
     */
    @Override
    public void onArrowDown() {
        if (currentActiveItem() != null)
            currentActiveItem().onArrowDown();
    }

    /**
     * Defines the listener behavior when the <code>Arrow Right</code> key is pressed
     */
    @Override
    public void onArrowRight() {
        if (currentActiveItem() != null)
            currentActiveItem().onArrowRight();
    }

    /**
     * Defines the listener behavior when the <code>Arrow Left</code> key is pressed
     */
    @Override
    public void onArrowLeft() {
        if (currentActiveItem() != null)
            currentActiveItem().onArrowLeft();
    }

}
