package it.polimi.ingsw.client.view.inputManagers;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.Constants;
import it.polimi.ingsw.shared.messages.fromClientToServer.ChooseToReloadMatchResponse;

public class ReloadGameInputManager extends InputManager {


    /**
     * Constructor to be called to handle an eventual game restore
     *
     * @param client the client to parse the
     */
    public ReloadGameInputManager(Client client) {
        super(client);
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
            if (input.equals(Constants.YES)) {
                reloadMatch(true);
                isWaitingForInput = false;
            } else {
                reloadMatch(false);
            }
        }
    }

    /**
     * Sends a {@linkplain ChooseToReloadMatchResponse} based on the user input
     *
     * @param choice true if the owner wants to reload the saved game, false otherwise
     */
    private void reloadMatch(boolean choice) {
        stopTimer();
        client.sendMessage(new ChooseToReloadMatchResponse(client.getUsername(), choice));
        client.setCurrentPlayer(false);
    }
}
