package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.network.message.request.fromClientToServer.PlayerMoveRequest;
import it.polimi.ingsw.network.message.request.fromClientToServer.SelectBuildingCellRequest;
import it.polimi.ingsw.network.message.response.fromServerToClient.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectActionCellInputManager extends InputManager {

    private enum State {
        MOVE, BUILD
    }

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
        if (isWaitingForInput) {
            List<Cell> selectedCell;
            input = cleanInput(input);
            switch (state) {
                case MOVE:
                    try {
                        int coord = Integer.parseInt(input);
                        if (coord < 1 || coord > 5) {
                            if (row == -1)
                                view.showErrorMessage("Please insert a valid number between 1 and 5 \nrow: ");
                            else
                                view.showErrorMessage("Please insert a valid number between 1 and 5, the row selected is: " + (row + 1) + "\ncol: ");
                        } else if (row == -1) {
                            row = coord - 1;
                            view.showSuccessMessage("col: ");
                        } else if (col == -1) {
                            col = coord - 1;
                            selectedCell = validCells.stream().filter(cell -> cell.getCoordX() == row && cell.getCoordY() == col).collect(Collectors.toList());
                            if (selectedCell.size() == 1) {
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
                        view.showErrorMessage("Please insert a valid number between 1 and 5");
                    }
                    break;
                case BUILD: //BuildAction
                    try {
                        int coord = Integer.parseInt(input);
                        if (coord < 1 || coord > 5) {
                            if (row == -1)
                                view.showErrorMessage("Please insert a valid number between 1 and 5 \nrow: ");
                            else
                                view.showErrorMessage("Please insert a valid number between 1 and 5, the row selected is: " + (row + 1) + "\ncol: ");
                        } else if (row == -1) {
                            row = coord - 1;
                            view.showSuccessMessage("col: ");
                        } else if (col == -1) {
                            col = coord - 1;
                            selectedCell = validCells.stream().filter(cell -> cell.getCoordX() == row && cell.getCoordY() == col).collect(Collectors.toList());
                            if (selectedCell.size() == 1) {
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
                        view.showErrorMessage("Please insert a valid number between 1 and 5");
                    }
                    break;
            }
        }
    }
}

