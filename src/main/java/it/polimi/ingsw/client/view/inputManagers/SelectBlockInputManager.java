package it.polimi.ingsw.client.view.inputManagers;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.network.MessageManagerParser;
import it.polimi.ingsw.shared.dataClasses.Block;

import java.util.ArrayList;
import java.util.List;

public class SelectBlockInputManager extends InputManager {

    private final MessageManagerParser parser;
    private final List<Block> possibleBlocks = new ArrayList<>();

    public SelectBlockInputManager(Client client, MessageManagerParser parser, List<Block> possibleBlocks) {
        super(client);
        this.parser = parser;
        this.possibleBlocks.addAll(possibleBlocks);
    }

    @Override
    public void manageInput(String input) {
        if (isWaitingForInput) {
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < possibleBlocks.size()) {
                    stopTimer();
                    parser.sendBuild(possibleBlocks.get(index));
                    isWaitingForInput = false;
                } else
                    view.showErrorMessage("Please insert a correct value");
            } catch (NumberFormatException e) {
                view.showErrorMessage("please insert a number");
            }
        }
    }
}
