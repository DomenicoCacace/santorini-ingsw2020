package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.view.Constants;

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
        if (input.equals(Constants.QUIT)) {
            stopTimer();
            client.stopConnection();
            new Thread(() -> Client.initClient(view)).start();
            return;
        }
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
