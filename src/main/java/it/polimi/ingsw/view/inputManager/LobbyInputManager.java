package it.polimi.ingsw.view.inputManager;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.network.message.request.fromClientToServer.CreateLobbyRequest;
import it.polimi.ingsw.network.message.request.fromClientToServer.JoinLobbyRequest;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.CLI;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LobbyInputManager extends InputManager {
    private enum State {
        CREATE_OR_JOIN, CREATE, JOIN,
    }

    public static final String CREATE_LOBBY = "1";
    public static final String JOIN_LOBBY = "2";
    private State state;
    private final ViewInterface view;
    private final Client client;
    private String lobbyName;
    private final MessageManagerParser messageManagerParser;
    private final Map<String, List<String>> lobbiesAvailable;

    public LobbyInputManager(ViewInterface view, Client client, Map<String, List<String>> lobbiesAvailable, MessageManagerParser messageManagerParser, boolean isJoining) {
        this.view = view;
        this.client = client;
        this.lobbiesAvailable = lobbiesAvailable;
        this.messageManagerParser = messageManagerParser;
        if(isJoining)
            this.state=State.JOIN;
        else
            this.state=State.CREATE_OR_JOIN;
    }

    @Override
    public void manageInput(String string) {
        if(isWaitingForInput) {
            switch (state) {
                case CREATE_OR_JOIN:
                    string = cleanInput(string);
                    manageLobbyOption(string);
                    break;
                case CREATE:
                    if (lobbyName == null)
                        onLobbyNameChosen(string);
                    else {
                        try {
                            int chosenSize = Integer.parseInt(cleanInput(string));
                            if (chosenSize != 2 && chosenSize != 3) {
                                view.showErrorMessage("Please insert a number between 2 and 3");
                                view.askLobbySize();
                            } else
                                onLobbySize(chosenSize);
                        } catch (NumberFormatException e) {
                            view.showErrorMessage("Please insert a number between 2 and 3");
                            view.askLobbySize();
                        }
                    }
                    break;
                case JOIN:
                    try {
                        int lobbiesOptionIndex = Integer.parseInt(cleanInput(string));
                        chosenIndexEvaluator(new LinkedList<>(this.lobbiesAvailable.keySet()), lobbiesOptionIndex - 1);
                    } catch (NumberFormatException e) {
                        view.showErrorMessage("Please insert a correct option");
                        view.chooseLobbyToJoin(lobbiesAvailable);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void chosenIndexEvaluator(List<String> lobbies, int index) {
        if (index == lobbies.size()) {
            messageManagerParser.enterLobby(lobbiesAvailable);
            return;
        }
        onLobbyChosen(lobbies.get((index)));
    }

    private void manageLobbyOption(String input) {
        if (input.equals(CREATE_LOBBY)) {
            this.state = State.CREATE;
            messageManagerParser.setCreatingLobby(true);
            messageManagerParser.setLookingForLobbies(false);
            view.askLobbyName();
        } else if (input.equals(JOIN_LOBBY) && lobbiesAvailable.keySet().size()>0) {  // Join existing lobby
            this.state = State.JOIN;
            messageManagerParser.setLookingForLobbies(true);
            view.chooseLobbyToJoin(lobbiesAvailable);
        } else {
            messageManagerParser.enterLobby(lobbiesAvailable);
        }

    }

    private void onLobbyNameChosen(String lobbyName) {
        this.lobbyName = lobbyName;
        view.askLobbySize();
    }

    private void onLobbySize(int chosenSize) {
        client.setCurrentPlayer(true);
        messageManagerParser.setChosenSize(chosenSize);
        isWaitingForInput=false;
        client.sendMessage(new CreateLobbyRequest(client.getUsername(), lobbyName, chosenSize));
        messageManagerParser.setCreatingLobby(false);
    }

    private void onLobbyChosen(String input) {
        client.setCurrentPlayer(true);
        messageManagerParser.setLookingForLobbies(false);
        isWaitingForInput = false;
        client.sendMessage(new JoinLobbyRequest(client.getUsername(), input));
        client.setCurrentPlayer(false);
    }
}
