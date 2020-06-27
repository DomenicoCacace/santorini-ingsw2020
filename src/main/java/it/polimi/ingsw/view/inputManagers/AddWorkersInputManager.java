package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.view.Constants;

public class AddWorkersInputManager extends InputManager {

    private static final int MIN_COORD = 1;
    private static final int MAX_COORD = 5;
    private final MessageManagerParser parser;
    private int col = -1;
    private int row = -1;

    public AddWorkersInputManager(Client client, MessageManagerParser parser) {
        super(client);
        this.parser = parser;
    }

    @Override
    public void manageInput(String input) {
        if (input.equals(Constants.QUIT)) {
            stopTimer();
            client.stopConnection();
            new Thread(() -> Client.initClient(view)).start();
            return;
        }
        if (isWaitingForInput) {
            try {
                int coord = Integer.parseInt(input);
                String errorMsg = "Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD;
                if (coord < MIN_COORD || coord > MAX_COORD) {
                    if (row == -1)
                        errorMsg += "\nrow: ";
                    else
                        errorMsg += ", the row selected is: " + (row + 1) + "\ncol: ";
                    view.showErrorMessage(errorMsg);
                } else if (row == -1) {
                    row = coord - 1;
                    startTimer(Constants.INPUT_TIMER);
                    view.printCol();
                } else if (col == -1) {
                    stopTimer();
                    col = coord - 1;
                    parser.addWorker(row, col);
                    isWaitingForInput = false;
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD);
            }
        }
    }
}


