package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseToReloadMatchResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles inputs regarding the god choice and the match reload
 */
public class GodChoiceInputManager extends InputManager {

    private enum State {
        CHOOSE_INITIAL_GODS, CHOOSE_PERSONAL_GOD,
    }

    private State state;
    private final int godsToChoose;
    private final List<GodData> availableGods;
    private final List<GodData> userChoice;

    /**
     * Constructor to be called to manage gods choice
     * @param client the client to manage the inputs for
     * @param availableGods the list of gods to choose from
     * @param godsToChoose the number of gods to choose
     */
    public GodChoiceInputManager(Client client, List<GodData> availableGods, int godsToChoose) {
        super(client);
        this.availableGods = availableGods;
        this.godsToChoose = godsToChoose;
        this.userChoice = new ArrayList<>(godsToChoose);
        if(godsToChoose == 1)
            state= State.CHOOSE_PERSONAL_GOD;
        else
            state = State.CHOOSE_INITIAL_GODS;
    }

    /**
     * Determines how to handle the received input based on the internal state
     * <p>
     *     <ul>
     *         <li>RELOADING: the user has to decide if it wants to reload a match (y/n)</li>
     *         <li>CHOOSE_INITIAL_GODS: the lobby owner has to choose the gods for all of the players</li>
     *         <li>CHOOSE_PERSONAL_GOD: the user has to choose its own god</li>
     *     </ul>
     * </p>
     * @param input the user input
     */
    @Override
    public void manageInput(String input) {
        GodData chosenGod;
        client.setCurrentPlayer(true);
        if (isWaitingForInput) {
            switch (state) {
                case CHOOSE_INITIAL_GODS:
                    chosenGod = askGod(input);
                    if (chosenGod != null) {
                        userChoice.add(chosenGod);
                        if (userChoice.size() == godsToChoose) {
                            client.setCurrentPlayer(true);
                            client.sendMessage(new ChooseInitialGodsResponse(client.getUsername(), userChoice));
                            client.setCurrentPlayer(false);
                            isWaitingForInput = false;
                        }
                    }
                    else
                        view.showErrorMessage("Invalid choice");

                    if (isWaitingForInput)
                        view.chooseGameGods(availableGods, godsToChoose - userChoice.size());
                    break;
                case CHOOSE_PERSONAL_GOD:
                    chosenGod = askGod(input);
                    if (chosenGod != null) {
                        client.setCurrentPlayer(true);
                        client.sendMessage(new ChooseYourGodResponse(client.getUsername(), chosenGod));
                        client.setCurrentPlayer(false);
                        isWaitingForInput = false;
                    }
                    else {
                        view.showErrorMessage("Invalid choice");
                        view.chooseUserGod(availableGods);
                    }
            }
        }
        client.setCurrentPlayer(false);
    }

    /**
     * Retrieves the god chosen by parsing the user input
     * @param input the input string
     * @return the chosen god
     */
    private GodData askGod(String input) {
        input = cleanInput(input);
        try {
            int godIndex = Integer.parseInt(cleanInput(input));
            if (godIndex > 0 && godIndex <= availableGods.size())
                return availableGods.remove(godIndex - 1);
            else
                return null;
        } catch (NumberFormatException e){
            return null;
        }
    }



}
