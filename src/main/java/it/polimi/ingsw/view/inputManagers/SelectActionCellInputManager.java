package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.network.message.request.fromClientToServer.SelectBuildingCellRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectActionCellInputManager extends InputManager {

    private static final int MIN_COORD = 1;
    private static final int MAX_COORD = 5;
    private final State state;
    private final List<Cell> validCells = new ArrayList<>();
    private final MessageManagerParser parser;
    private int row = -1;
    private int col = -1;

    public SelectActionCellInputManager(Client client, List<Cell> validCells, boolean isMoving, MessageManagerParser parser) {
        super(client);
        this.validCells.addAll(validCells);
        this.parser = parser;
        if (isMoving)
            state = State.MOVE;
        else
            state = State.BUILD;
    }

    @Override
    public void manageInput(String input) {
        if (input.equals(QUIT)) {
            stopTimer();
            client.stopConnection();
            new Thread(() -> Client.initClient(view)).start();
        } else if (isWaitingForInput) {
            List<Cell> selectedCell;
            input = cleanInput(input);
            switch (state) {
                case MOVE:
                    try {
                        int coord = Integer.parseInt(input);
                        if (coord < MIN_COORD || coord > MAX_COORD) {
                            if (row == -1)
                                view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD + "\nrow: ");
                            else
                                view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD + ", the row selected is: " + (row + 1) + "\ncol: ");
                        } else if (row == -1) {
                            row = coord - 1;
                            view.showSuccessMessage("col: ");
                            startTimer(60);
                        } else if (col == -1) {
                            col = coord - 1;
                            selectedCell = validCells.stream().filter(cell -> cell.getCoordX() == row && cell.getCoordY() == col).collect(Collectors.toList());
                            if (selectedCell.size() == 1) {
                                stopTimer();
                                isWaitingForInput = false;
                                parser.sendMove(selectedCell.get(0));
                            } else {
                                view.showErrorMessage("Please select a valid cell!!");
                                col = -1;
                                row = -1;
                                parser.printOnMove(validCells);
                            }
                        }
                    } catch (NumberFormatException e) {
                        view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD);
                    }
                    break;
                case BUILD: //BuildAction
                    try {
                        int coord = Integer.parseInt(input);
                        if (coord < MIN_COORD || coord > MAX_COORD) {
                            if (row == -1)
                                view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD + "\nrow: ");
                            else
                                view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD + ", the row selected is: " + (row + 1) + "\ncol: ");
                        } else if (row == -1) {
                            row = coord - 1;
                            view.showSuccessMessage("col: ");
                            startTimer(60);
                        } else if (col == -1) {
                            col = coord - 1;
                            selectedCell = validCells.stream().filter(cell -> cell.getCoordX() == row && cell.getCoordY() == col).collect(Collectors.toList());
                            if (selectedCell.size() == 1) {
                                stopTimer();
                                isWaitingForInput = false;
                                parser.setSelectedCell(selectedCell.get(0));
                                client.sendMessage(new SelectBuildingCellRequest(client.getUsername(), selectedCell.get(0)));
                            } else {
                                view.showErrorMessage("Please select a valid cell!!");
                                parser.printOnBuild(validCells);
                                col = -1;
                                row = -1;
                            }
                        }
                    } catch (NumberFormatException e) {
                        view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD);
                    }
                    break;
            }
        }
    }

    private enum State {
        MOVE, BUILD
    }
}

