package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.view.Constants;

public class SelectWorkerInputManager extends InputManager {

    private static final int MIN_COORD = 1;
    private static final int MAX_COORD = 5;
    private final MessageManagerParser parser;
    private int row = -1;
    private int col = -1;

    public SelectWorkerInputManager(Client client, MessageManagerParser parser) {
        super(client);
        this.parser = parser;
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
                    startTimer(60);
                } else if (col == -1) {
                    stopTimer();
                    col = coord - 1;
                    isWaitingForInput = false;
                    parser.chooseWorker(row, col);
                    row = -1;
                    col = -1;
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please insert a valid number between " + MIN_COORD + " and " + MAX_COORD);
            }
        }
    }
}
