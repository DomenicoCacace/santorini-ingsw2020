package it.polimi.ingsw.network.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.God;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseInitialGodsRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseStartingPlayerRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseYourGodRequest;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseNumberOfPlayerResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.view.ViewInterface;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MessageParser {

    private final Client client;
    private ViewInterface view;

    //To debug
    private Scanner input;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int chosenSize;
    private final Map<String, GodData> godsMap;

    public MessageParser(Client client) {
        this.client = client;
        this.input = new Scanner(System.in);
        this.godsMap = new HashMap<>();
        //I need a map to link the name of the god with his godData (we can use lambdas with filter instead)
        List<God> gods  = new ArrayList<>();
        try {
            gods =  objectMapper.readerFor(new TypeReference<List<God>>() {
            }).readValue(new File("GodsConfigFile.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gods.forEach(god ->{
            godsMap.put(god.getName(), god.buildDataClass());
        });

    }

    public void parseMessageFromServerToClient(Message message) throws IOException {
        switch (message.getContent()) {
            case LOGIN:
                if (((LoginResponse) message).getOutcome().equals("OK")) {
                    System.out.println("Login Succesfull");
                    // view.displayLobby;
                } else {
                    System.out.println("Error " + ((LoginResponse) message).getOutcome() + "\n resend Login" );
                    client.setUsername(input.nextLine());
                    // view.displayLoginError(outcome);
                    // view.displayLogin;
                }
                break;
            case PLAYER_MOVE:
                if (((PlayerMoveResponse) message).getOutcome().equals("OK")) {
                    //view.displayGameboard(payload);
                    //payload to be saved internally on the view
                } else{
                    //view.displayIllegalActionError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case PLAYER_BUILD:
                if (((PlayerBuildResponse) message).getOutcome().equals("OK")) {
                    //view.displayGameboard(payload);
                    //payload to be saved internally on the view
                } else{
                    //view.displayIllegalActionError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case END_TURN:
                if (((EndTurnResponse) message).getOutcome().equals("OK")) {
                    //view.displayPlayableInterface
                    //view.displayNonPlayableInterface(payload);
                    client.setCurrentPlayer(((EndTurnResponse) message).getOutcome().equals(client.getUsername()));
                } else{
                    //view.displayIllegalTurnEndingError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case ADD_WORKER:
                if (((AddWorkerResponse) message).getOutcome().equals("OK")) {
                    //view.displayGameboard(payload)
                }
                else {
                    //view.displayError(outcome)
                }
                break;
            case CHOOSE_INITIAL_GODS:
                client.setCurrentPlayer(true);
                List<GodData> gods = (((ChooseInitialGodsRequest) message).getGods());
                System.out.println("Choose " + chosenSize + " gods between:");
                //prints all the gods: view related
                gods.forEach(god ->{
                    System.out.println("-" + god.getName());
                });
                input = new Scanner(System.in);
                List<GodData> chosenGods = new LinkedList<>();
                for(int i = 0; i < chosenSize; i++) {
                    String godName = input.nextLine();
                    chosenGods.add(godsMap.get(godName));
                }
                Message message1 = new ChooseInitialGodsResponse(client.getUsername(), chosenGods);
                client.sendMessage(message1);
                client.setCurrentPlayer(false);
                //view.diplayAllGods
                break;
            case WINNER_DECLARED:
                //view.displayWinner
                break;
            case PLAYER_LOST:
                //view.displayNonPlayableInterface
                break;
            case PLAYER_REMOVED:
                //view.displayGameboard(payload)
                break;
            case CHOOSE_PLAYER_NUMBER:
                client.setCurrentPlayer(true);
                System.out.println("Choose the number of players");
                chosenSize = input.nextInt();
                message = new ChooseNumberOfPlayerResponse(client.getUsername(), chosenSize);
                client.sendMessage(message);
                //view.displayChooseNumberOfPlayer
                break;
            case CHOOSE_GOD:
                client.setCurrentPlayer(true);
                //View related: prints all the available gods
                System.out.println("Choose your God between:");
                ((ChooseYourGodRequest) message).getGods().forEach(godData -> System.out.println("-" + godData.getName()));
                input = new Scanner(System.in);
                String inputString = input.nextLine();
                GodData chosenGod = godsMap.get(inputString);
                message=new ChooseYourGodResponse(client.getUsername(), chosenGod);
                client.sendMessage(message);
                client.setCurrentPlayer(false);
                //view.displayWaitingLobby
                break;
            case STARTING_PLAYER:
                client.setCurrentPlayer(true);
                System.out.println("Choose the starting player: " + ((ChooseStartingPlayerRequest) message).getPayload());
                input = new Scanner(System.in);
                inputString = input.nextLine();
                message = new ChooseStartingPlayerResponse(client.getUsername(), inputString);
                client.sendMessage(message);
                client.setCurrentPlayer(false);
                break;
            case SELECT_WORKER:
                if(((SelectWorkerResponse) message).getOutcome().equals("OK")){
                    //view.displayChooseAction
                } else {
                    //view.displayError(outcome)
                }
                break;
            case WALKABLE_CELLS:
                if(((WalkableCellsResponse) message).getOutcome().equals("OK")){
                    //view.displayCellsSuperFiche
                } else {
                    //view.displayError(outcome)
                }
                break;
            case BUILDABLE_CELLS:
                if(((BuildableCellsResponse) message).getOutcome().equals("OK")){
                    //view.displayCellsSuperFiche
                } else {
                    //view.displayError(outcome)
                }
                break;
            case CHOSEN_GODS:
                if(((ChosenGodsResponse) message).getOutcome().equals("OK")){
                    //view.displayChosenGods
                } else {
                    //view.displayError(outcome)
                }
                break;
            case GAME_START:
                //view.displayGame(payload)
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.getContent());
        }
    }
}
