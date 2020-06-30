package it.polimi.ingsw.client.view.inputManagers;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.network.MessageManagerParser;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.PossibleActions;

import java.util.ArrayList;
import java.util.List;

public class SelectActionInputManager extends InputManager {

    private final MessageManagerParser parser;
    private final List<PossibleActions> possibleActions = new ArrayList<>();
    private final List<Cell> workersCells = new ArrayList<>();

    /**
     * Default constructor
     *
     * @param client the client to manage the inputs for
     */
    public SelectActionInputManager(Client client, List<PossibleActions> possibleActions, List<Cell> workersCells, MessageManagerParser parser) {
        super(client);
        this.possibleActions.addAll(possibleActions);
        this.workersCells.addAll(workersCells);
        this.parser = parser;
    }

    @Override
    public void manageInput(String input) {
        if (isWaitingForInput) {
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < possibleActions.size()) {
                    stopTimer();
                    new Thread(() -> parser.messageToSend(possibleActions.get(index), workersCells)).start();
                    isWaitingForInput = false;
                } else
                    view.showErrorMessage("Please insert a valid option");
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please insert a valid option");
            }
        }
    }
}
