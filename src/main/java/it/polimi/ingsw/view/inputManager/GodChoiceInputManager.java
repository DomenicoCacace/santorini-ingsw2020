package it.polimi.ingsw.view.inputManager;

import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.view.ViewInterface;

import java.util.ArrayList;
import java.util.List;

public class GodChoiceInputManager extends InputManager {
    private enum State {
        RELOADING, CHOOSE_INITIAL_GODS, CHOOSE_PERSONAL_GOD,
    }

    private State state;
    private int godsToChoose;
    private final Client client;
    private final ViewInterface view;
    private final List<GodData> chosenGods;

    public GodChoiceInputManager(ViewInterface view, Client client, List<GodData> chosenGods, int godsToChoose) {
        this.client = client;
        this.view = view;
        this.chosenGods = chosenGods;
        if(godsToChoose == 1)
            state= State.CHOOSE_PERSONAL_GOD;
        else
            state = State.CHOOSE_INITIAL_GODS;
    }

    public GodChoiceInputManager(Client client, ViewInterface view) {
        state=State.RELOADING;
        chosenGods=new ArrayList<>();
        this.client = client;
        this.view = view;
    }

    @Override
    public void manageInput(String string) {
        //Message chosenGodMessage = new ChooseYourGodResponse(client.getUsername(), chosenGod);
        //client.sendMessage(chosenGodMessage);
        client.setCurrentPlayer(true);
        //Reload messageResponse = new ChooseToReloadMatchResponse(client.getUsername(), boolean);
    }
}
