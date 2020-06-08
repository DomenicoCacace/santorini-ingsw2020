package it.polimi.ingsw.view.cli.console.prettyPrinters;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.CursorPosition;
import it.polimi.ingsw.view.cli.console.KeyEventListener;
import it.polimi.ingsw.view.cli.console.graphics.ListPane;
import it.polimi.ingsw.view.cli.console.graphics.SingleChoiceListDialog;
import it.polimi.ingsw.view.cli.console.graphics.GridOverlay;
import it.polimi.ingsw.view.cli.console.graphics.components.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection of WindowItems and utility methods to decorate the game board, giving information about the game status
 */
public class FancyPrinterBoardUtils extends Window implements KeyEventListener  {

    private final FancyPrinter printer;
    private final CursorPosition boardOffset;

    private final GridOverlay gridOverlay;
    private ListPane playerInfo;
    private boolean playerInfoSet = false;



    public FancyPrinterBoardUtils(FancyPrinter printer) throws IOException {
        super(printer.console, "BoardPrinter");
        Console.in.disableConsoleInput();
        this.printer = printer;
        this.initCoord.setCoordinates(0, 0);
        boardOffset = new CursorPosition(1, 1);
        this.addToBackground(new PrintableObject(printer.getClass().getResourceAsStream(properties.getProperty("emptyBoardPath")), printer.boardWidth, printer.boardHeight), boardOffset);
        gridOverlay = new GridOverlay(printer.console, boardOffset, printer, "BoardOverlay");
        passiveItems.add(gridOverlay);
    }

    /**
     * <i>boardOffset</i> getter
     *
     * @return the board initial coordinates
     */
    public CursorPosition getBoardOffset() {
        return boardOffset;
    }

    /**
     * Enables the user to select a cell, using a {@linkplain GridOverlay}
     */
    public void enableGridInput() {
        activeItems.addFirst(gridOverlay);
        gridOverlay.enable();
    }

    public void setPlayerData(List<PlayerData> playerData) {
        if (!playerInfoSet) {
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

                playerInfo = new ListPane(this, CursorPosition.offset(boardOffset, 0, printer.boardWidth + 2), 65, 20, players, "PlayersDetails");
                playerInfo.show();
                activeItems.addLast(playerInfo);
                playerInfoSet = true;
            }
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
        new SingleChoiceListDialog("", "Select an action", this, options, 65, 13, new CursorPosition(43, 124)).show();
    }

    public void showGameBoard() {
        Console.out.drawMatrix(printer.cachedBoard, boardOffset);
    }

    public void showGameBoard(List<Cell> toHighlight) {
        String[][] tempBoard = printer.cloneMatrix(printer.cachedBoard);
        for (Cell cell : toHighlight)
            printer.highlight(cell, tempBoard);
        Console.out.drawMatrix(tempBoard, boardOffset);
    }

    /**
     * Defines the listener behavior when the <code>Enter</code> key is pressed
     */
    @Override
    public void onCarriageReturn() {
        currentInteractiveItem().onCarriageReturn();
        if (currentInteractiveItem().equals(gridOverlay)) {
            activeItems.remove(gridOverlay);
        }
    }


    /**
     * Defines the listener behavior when the <code>Tab</code> key is pressed
     */
    @Override
    public void onTab() {
        nextInteractiveItem();
        currentInteractiveItem().onSelect();
    }

    /**
     * Defines the listener behavior when the <code>Arrow Up</code> key is pressed
     */
    @Override
    public void onArrowUp() {
        if (currentInteractiveItem() != null)
            currentInteractiveItem().onArrowUp();
    }

    /**
     * Defines the listener behavior when the <code>Arrow Down</code> key is pressed
     */
    @Override
    public void onArrowDown() {
        if (currentInteractiveItem() != null)
            currentInteractiveItem().onArrowDown();
    }

    /**
     * Defines the listener behavior when the <code>Arrow Right</code> key is pressed
     */
    @Override
    public void onArrowRight() {
        if (currentInteractiveItem() != null)
            currentInteractiveItem().onArrowRight();
    }

    /**
     * Defines the listener behavior when the <code>Arrow Left</code> key is pressed
     */
    @Override
    public void onArrowLeft() {
        if (currentInteractiveItem() != null)
            currentInteractiveItem().onArrowLeft();
    }

}
