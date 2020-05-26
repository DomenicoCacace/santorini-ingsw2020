package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.view.Constants;

import java.util.ArrayList;
import java.util.List;

public class SelectActionInputManager extends InputManager {

    private final MessageManagerParser parser;
    List<PossibleActions> possibleActions = new ArrayList<>();

    /**
     * Default constructor
     *
     * @param client the client to manage the inputs for
     */
    public SelectActionInputManager(Client client, List<PossibleActions> possibleActions, MessageManagerParser parser) {
        super(client);
        this.possibleActions.addAll(possibleActions);
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
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < possibleActions.size()) {
                    stopTimer();
                    new Thread(() -> parser.messageToSend(possibleActions.get(index))).start();
                    isWaitingForInput = false;
                } else
                    view.showErrorMessage("Please insert a valid option");
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please insert a valid option");
            }
        }
    }
}
