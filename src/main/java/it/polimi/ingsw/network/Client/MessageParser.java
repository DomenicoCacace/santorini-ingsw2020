package it.polimi.ingsw.network.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseStartingPlayerRequest;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseNumberOfPlayerResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MessageParser {

    private final Client client;
    private ViewInterface view;

    //To debug
    private Scanner input;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int chosenSize;

    public MessageParser(Client client) {
        this.client = client;
        this.input = new Scanner(System.in);
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
                System.out.println(" choose gods");
                List<GodData> gods = new ArrayList<>();
                input = new Scanner(System.in);
                for(int i = 0; i < chosenSize; i++) {
                    String god = input.nextLine();
                    gods.add(objectMapper.readerFor(GodData.class).readValue(god));
                }
                Message message1 = new ChooseInitialGodsResponse(client.getUsername(), gods);
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
                System.out.println("Choose players's number");
                chosenSize = input.nextInt();
                message = new ChooseNumberOfPlayerResponse(client.getUsername(), chosenSize);
                client.sendMessage(message);
                //view.displayChooseNumberOfPlayer
                break;
            case CHOOSE_GOD:
                client.setCurrentPlayer(true);
                System.out.println("Choose your God");
                input = new Scanner(System.in);
                String inputString = input.nextLine();
                GodData chosenGod = objectMapper.readerFor(GodData.class).readValue(inputString);
                message=new ChooseYourGodResponse(client.getUsername(), chosenGod);
                client.sendMessage(message);
                client.setCurrentPlayer(false);
                //view.displayWaitingLobby
                break;
            case STARTING_PLAYER:
                client.setCurrentPlayer(true);
                System.out.println("choose the starting plater: " + ((ChooseStartingPlayerRequest) message).getPayload());
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
