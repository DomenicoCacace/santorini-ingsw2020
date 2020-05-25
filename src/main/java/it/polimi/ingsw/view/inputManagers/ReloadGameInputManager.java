package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseToReloadMatchResponse;

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
        if (input.equals(QUIT)) {
            stopTimer();
            client.stopConnection();
            new Thread(() -> Client.initClient(view)).start();
        } else if (isWaitingForInput) {
            if (input.equals("y")) {    //TODO: use static constant
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
