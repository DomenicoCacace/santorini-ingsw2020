package it.polimi.ingsw.view.inputManagers;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.MessageManagerParser;
import it.polimi.ingsw.network.message.fromClientToServer.CreateLobbyRequest;
import it.polimi.ingsw.network.message.fromClientToServer.JoinLobbyRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Handles inputs regarding logging onto the server and managing lobbies
 */
public class LobbyInputManager extends InputManager {

    public static final String CREATE_LOBBY = "1";
    public static final String JOIN_LOBBY = "2";
    private final MessageManagerParser messageManagerParser;
    private final Map<String, List<String>> lobbiesAvailable;
    private State state;
    private String lobbyName;

    public LobbyInputManager(Client client, Map<String, List<String>> lobbiesAvailable, MessageManagerParser messageParser, boolean isJoining) {
        super(client);
        this.lobbiesAvailable = lobbiesAvailable;
        this.messageManagerParser = messageParser;
        if (isJoining)
            this.state = State.JOIN;
        else
            this.state = State.CREATE_OR_JOIN;
    }

    @Override
    public void manageInput(String input) {
        if (input.equals(QUIT)) {
            stopTimer();
            client.stopConnection();
            new Thread(() -> Client.initClient(view)).start();
        } else if (isWaitingForInput) {
            switch (state) {
                case CREATE_OR_JOIN:
                    input = cleanInput(input);
                    manageLobbyOption(input);
                    break;
                case CREATE:
                    if (lobbyName == null)
                        onLobbyNameChosen(input);
                    else {
                        try {
                            int chosenSize = Integer.parseInt(cleanInput(input));
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
                        int lobbiesOptionIndex = Integer.parseInt(cleanInput(input));
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
            stopTimer();
        }
        else if(index < 0 || index > lobbies.size()) {
            view.showErrorMessage("Insert a valid option!");
            view.chooseLobbyToJoin(lobbiesAvailable);
        }
        else
            onLobbyChosen(lobbies.get((index)));
    }

    private void manageLobbyOption(String input) {
        if (input.equals(CREATE_LOBBY)) {
            stopTimer();
            this.state = State.CREATE;
            messageManagerParser.setCreatingLobby(true);
            messageManagerParser.setLookingForLobbies(false);
            view.askLobbyName();
            startTimer(60);
        } else if (input.equals(JOIN_LOBBY) && lobbiesAvailable.keySet().size() > 0) {  // Join existing lobby
            stopTimer();
            this.state = State.JOIN;
            messageManagerParser.setLookingForLobbies(true);
            view.chooseLobbyToJoin(lobbiesAvailable);
            startTimer(60);
        } else {
            messageManagerParser.enterLobby(lobbiesAvailable);
        }

    }

    private void onLobbyNameChosen(String lobbyName) {
        stopTimer();
        this.lobbyName = lobbyName;
        view.askLobbySize();
        startTimer(60);
    }

    private void onLobbySize(int chosenSize) {
        messageManagerParser.setChosenSize(chosenSize);
        isWaitingForInput = false;
        stopTimer();
        client.sendMessage(new CreateLobbyRequest(client.getUsername(), lobbyName, chosenSize));
        messageManagerParser.setCreatingLobby(false);
    }

    private void onLobbyChosen(String input) {
        messageManagerParser.setLookingForLobbies(false);
        isWaitingForInput = false;
        stopTimer();
        client.sendMessage(new JoinLobbyRequest(client.getUsername(), input));
    }

    private enum State {
        CREATE_OR_JOIN, CREATE, JOIN,
    }
}
