package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;

import java.util.ArrayList;
import java.util.List;

public class ChooseStartingPlayerInputManager extends InputManager {

    private final List<String> usernames = new ArrayList<>();



    /**
     * Default constructor
     *
     * @param client the client to manage the inputs for
     */
    public ChooseStartingPlayerInputManager(Client client, List<String> usernames) {
        super(client);
        this.usernames.addAll(usernames);
    }

    @Override
    public void manageInput(String input) {
        if (isWaitingForInput) {
            input = cleanInput(input);
            try{
                int index = Integer.parseInt(input) -1;
                if(index>=0 && index < usernames.size()) {
                    client.setCurrentPlayer(true); //FIXME: We can change where we set the currentPlayer
                    client.sendMessage(new ChooseStartingPlayerResponse(client.getUsername(), usernames.get(index)));
                    client.setCurrentPlayer(false);
                }
                else {
                    view.showErrorMessage("Please insert a valid name");
                    view.chooseStartingPlayer(usernames);
                }
            } catch (NumberFormatException e){
                view.chooseStartingPlayer(usernames);
            }
        }
    }
}
