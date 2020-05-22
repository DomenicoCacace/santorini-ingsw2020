package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.BuildableCellsRequest;
import it.polimi.ingsw.network.message.request.fromClientToServer.EndTurnRequest;
import it.polimi.ingsw.network.message.request.fromClientToServer.WalkableCellsRequest;


import java.util.ArrayList;
import java.util.List;

public class SelectActionInputManager extends InputManager {

    List<PossibleActions> possibleActions = new ArrayList<>();
    private final MessageManagerParser parser;

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
        if(isWaitingForInput){
            input = cleanInput(input);
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < possibleActions.size()) {
                    new Thread(() -> parser.messageToSend(possibleActions.get(index))).start(); //TODO: remove deadlock -- Look if there's a better solution
                    isWaitingForInput = false;
                }
                else
                    view.showErrorMessage("Please insert a valid option");
            } catch (NumberFormatException e) {
                view.showErrorMessage("Please insert a valid option");
            }
        }
    }
}
