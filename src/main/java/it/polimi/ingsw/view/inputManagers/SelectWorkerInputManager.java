package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;

public class SelectWorkerInputManager extends InputManager{
    private final MessageManagerParser parser;
    private int row = -1;
    private int col = -1;

    public SelectWorkerInputManager(Client client, MessageManagerParser parser) {
        super(client);
        this.parser = parser;
    }

    @Override
    public void manageInput(String input) {
        if(isWaitingForInput){
            input = cleanInput(input);
            try {
                int coord = Integer.parseInt(input);
                if (coord < 1 || coord > 5) {
                    if (row == -1)
                        view.showErrorMessage("Please insert a valid number between 1 and 5 \nrow: ");
                    else
                        view.showErrorMessage("Please insert a valid number between 1 and 5, the row selected is: " + (row +1) + "\ncol: ");
                }
                else if (row == -1) {
                    row = coord - 1;
                    view.showSuccessMessage("col: ");
                }
                else if (col == -1) {
                    col = coord - 1;
                    parser.chooseWorker(row, col);
                    isWaitingForInput = false;
                    row = -1;
                    col = -1;
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please insert a valid number between 1 and 5");
            }
        }
    }

}
