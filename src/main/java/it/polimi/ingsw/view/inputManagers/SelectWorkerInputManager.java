package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.view.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SelectWorkerInputManager extends InputManager {

    private static final int MIN_COORD = 1;
    private static final int MAX_COORD = 5;
    private final MessageManagerParser parser;
    private int row = -1;
    private int col = -1;
    private final List<Cell> workerCells = new ArrayList<>();

    public SelectWorkerInputManager(Client client,List<Cell> workerCells, MessageManagerParser parser) {
        super(client);
        this.parser = parser;
        this.workerCells.addAll(workerCells);
    }

    @Override
    public void manageInput(String input) {
        if (input.equals(Constants.QUIT)) {
            stopTimer();
            client.stopConnection();
            new Thread(() -> Client.initClient(view)).start();
        } else if (isWaitingForInput) {
            input = cleanInput(input);
            try {
                int coord = Integer.parseInt(input);
                if (coord < MIN_COORD || coord > MAX_COORD) {
                    if (row == -1)
                        view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD + "\nrow: ");
                    else
                        view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD + ", the row selected is: " + (row + 1) + "\ncol: ");
                } else if (row == -1) {
                    row = coord - 1;
                    view.printCol();
                    startTimer(60);
                } else if (col == -1) {
                    col = coord - 1;
                    Optional<Cell> workerCell = workerCells.stream().filter(cell -> cell.getCoordX() == row && cell.getCoordY() == col).findFirst();
                    if (workerCell.isPresent()) {
                        stopTimer();
                        isWaitingForInput = false;
                        parser.chooseWorker(workerCell.get().getOccupiedBy());
                    } else {
                        view.showErrorMessage("Please select a valid cell!!");
                        col = -1;
                        row = -1;
                        view.chooseWorker(workerCells);
                    }
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD);
            }
        }
    }

    @Override
    public void setWaitingForInput(boolean waitingForInput) {
        super.setWaitingForInput(waitingForInput);
        view.chooseWorker(workerCells);
    }
}
