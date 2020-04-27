package it.polimi.ingsw.network.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseInitialGodsRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseStartingPlayerRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseWorkerPositionRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseYourGodRequest;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseNumberOfPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.view.ViewInterface;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MessageParser {

    private final Client client;
    private ViewInterface view;

    private Scanner input;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int chosenSize;
    private final Map<String, GodData> godsMap;
    private List<Cell> gameboard;
    private Worker selectedWorker;
    private Cell selectedCell;

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
                    System.out.println("Error " + ((LoginResponse) message).getOutcome() + "\n" );
                    if(!((LoginResponse) message).getOutcome().equals("Match already started")) {
                        System.out.println("Resend login");
                        client.setUsername(input.nextLine());
                    }
                    // view.displayLoginError(outcome);
                    // view.displayLogin;
                }
                break;
            case PLAYER_MOVE:
                if (((PlayerMoveResponse) message).getOutcome().equals("OK")) {
                    System.out.println(((PlayerMoveResponse) message).getPayload().toString());
                    gameboard = ((PlayerMoveResponse) message).getPayload();
                    //view.displayGameboard(payload);
                    //payload to be saved internally on the view
                } else{
                    //view.displayIllegalActionError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case PLAYER_BUILD:
                if (((PlayerBuildResponse) message).getOutcome().equals("OK")) {
                    System.out.println(((PlayerBuildResponse) message).getPayload().toString());
                    gameboard = ((PlayerBuildResponse) message).getPayload();
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
                    client.setCurrentPlayer(((EndTurnResponse) message).getPayload().equals(client.getUsername()));
                    if(client.getUsername().equals(((EndTurnResponse) message).getPayload()))
                        chooseWorker();
                } else{
                    //view.displayIllegalTurnEndingError(outcome)
                    //view.displayBoard(oldBoard)
                }
                break;
            case WORKER_POSITION:
                client.setCurrentPlayer(true);
                System.out.println("place your workers");
                input = new Scanner(System.in);
                int coordx = input.nextInt();
                while (coordx < 0 || coordx > 4) {
                    System.out.println("invalid coord");
                    coordx = input.nextInt();
                }
                int coordy = input.nextInt();
                while (coordy < 0 || coordy > 4) {
                    System.out.println("invalid coord");
                    coordy = input.nextInt();
                }
                Message message1 = new AddWorkerRequest(client.getUsername(),
                        ((ChooseWorkerPositionRequest) message).getPayload().get(5*coordx + coordy));
                client.sendMessage(message1);
                client.setCurrentPlayer(false);
                break;
            case ADD_WORKER:
                if (((AddWorkerResponse) message).getOutcome().equals("OK")) {
                    System.out.println("Worker added succesfully" + ((AddWorkerResponse) message).getPayload().toString());
                    //view.displayGameboard(payload)
                }
                else {
                    System.out.println("can't place worker in that cell");
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
                message1 = new ChooseInitialGodsResponse(client.getUsername(), chosenGods);
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
                client.setCurrentPlayer(true);
                if(((SelectWorkerResponse) message).getOutcome().equals("OK")) {
                    selectedWorker = ((SelectWorkerResponse) message).getSelectedWorker();
                    System.out.println(((SelectWorkerResponse) message).getPossibleActions().toString());
                    do {
                        System.out.println("Choose a correct action");
                        input = new Scanner(System.in);
                        inputString = input.nextLine();
                    } while (!compare(inputString, ((SelectWorkerResponse) message).getPossibleActions()));

                    messageToSend(inputString);
                //view.displayChooseAction
                } else {
                    System.out.println("Wrong worker selected");
                    chooseWorker();
                    //view.displayError(outcome)
                }
                client.setCurrentPlayer(false);
                break;
            case WALKABLE_CELLS:
                client.setCurrentPlayer(true);
                if(((WalkableCellsResponse) message).getOutcome().equals("OK")){
                    if(((WalkableCellsResponse) message).getPayload().size() == 0)
                        System.out.println("No walkable cells available");
                    else {
                        System.out.println(((WalkableCellsResponse) message).getPayload());
                        int xMove, yMove;
                        do {
                            System.out.println("Select the cell where you want to go");
                            input = new Scanner(System.in);
                            xMove = input.nextInt();
                            yMove = input.nextInt();
                        } while(!isInsideAvailableCells(xMove, yMove, ((WalkableCellsResponse) message).getPayload()));
                        message = new PlayerMoveRequest(client.getUsername(), gameboard.get(5*xMove + yMove), selectedWorker);
                        client.sendMessage(message);
                        client.setCurrentPlayer(false);
                    }
                    //view.displayCellsSuperFiche
                } else {
                    //we should never enter here
                    //view.displayError(outcome)
                }
                break;
            case BUILDABLE_CELLS:
                client.setCurrentPlayer(true);
                if(((BuildableCellsResponse) message).getOutcome().equals("OK")){
                    if(((BuildableCellsResponse) message).getPayload().size() == 0)
                        System.out.println("No buildable cells available");
                    else
                        System.out.println(((BuildableCellsResponse) message).getPayload());
                    int xBuild, yBuild;
                    do {
                        System.out.println("Select the cell where you want to build");
                        input = new Scanner(System.in);
                        xBuild = input.nextInt();
                        yBuild = input.nextInt();
                    } while(!isInsideAvailableCells(xBuild, yBuild, ((BuildableCellsResponse) message).getPayload()));
                    selectedCell = gameboard.get(5*xBuild + yBuild);
                    Message message2 = new SelectBuildingCellRequest(client.getUsername(), selectedCell);
                    client.sendMessage(message2);
                    client.setCurrentPlayer(false);
                    //view.displayCellsSuperFiche
                } else {
                    //we should never enter here
                    //view.displayError(outcome)
                }
                break;
            case SELECT_BUILDING_CELL:
                client.setCurrentPlayer(true);
                System.out.println(((SelectBuildingCellResponse) message).getBlocks().toString());
                String block;
                do {
                    input = new Scanner(System.in);
                    block = input.nextLine();
                    //TODO: close input in all the scanners
                } while(!isValidBlock(block,((SelectBuildingCellResponse) message).getBlocks()));

                String chosenBlock = block;
                Block toBuildBlock = ((SelectBuildingCellResponse) message).getBlocks().stream().filter(b -> b.toString().equals(chosenBlock))
                                        .collect(Collectors.toList()).get(0);

                message = new PlayerBuildRequest(client.getUsername(), selectedCell, toBuildBlock ,selectedWorker);
                client.sendMessage(message);

                client.setCurrentPlayer(false);
                break;
            case CHOSEN_GODS: //TODO: review this shit
                if(((ChosenGodsResponse) message).getOutcome().equals("OK")){
                    //view.displayChosenGods
                } else {
                    //view.displayError(outcome)
                }
                break;
            case GAME_START:
                System.out.println("\n The Game Started! \n" + ((GameStartResponse)message).getPayload().getBoard().toString());
                gameboard = ((GameStartResponse) message).getPayload().getBoard();
                if(((GameStartResponse)message).getPayload().getCurrentTurn().getCurrentPlayer().getName().equals(client.getUsername())){
                    chooseWorker();
                }
                //view.displayGame(payload)
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.getContent());
        }
    }

    private void chooseWorker() {
        int coordx;
        int coordy;
        client.setCurrentPlayer(true);
        System.out.println("Select your worker");
        input = new Scanner(System.in);
        Cell selectedCell;
        do {
            coordx = input.nextInt();
            while (coordx < 0 || coordx > 4) {
                System.out.println("invalid coord");
                coordx = input.nextInt();
            }
            coordy = input.nextInt();
            while (coordy < 0 || coordy > 4) {
                System.out.println("invalid coord");
                coordy = input.nextInt();
            }
            selectedCell = gameboard.get(5 * coordx + coordy);
        } while (selectedCell.getOccupiedBy()==null); //TODO: remove other worker from selection
        selectedWorker = selectedCell.getOccupiedBy();
        Message message = new SelectWorkerRequest(client.getUsername(), selectedWorker);
        client.sendMessage(message);
        client.setCurrentPlayer(false);
    }

    private boolean compare(String action, List<PossibleActions> possibleActions){
        List<String> actionsAvailable = new ArrayList<>();
        possibleActions.forEach(s -> actionsAvailable.add(s.toString()));
        for(String string : actionsAvailable) {
            if (string.equals(action))
                return true;
        }
        return false;
    }

    private void messageToSend(String input) {
        Message message = null;
        if(input.equals("Move")){
            message = new WalkableCellsRequest(client.getUsername());
            client.sendMessage(message);
        } else if(input.equals("Build")){
            message = new BuildableCellsRequest(client.getUsername());
            client.sendMessage(message);
        } else if(input.equals("Pass turn")){
            message = new EndTurnRequest(client.getUsername());
            client.sendMessage(message);
        } else if(input.equals("Select other worker")){
            chooseWorker();
        }
    }

    private boolean isInsideAvailableCells(int x, int y, List<Cell> legalCells) {
        for(Cell cell : legalCells){
            if(x == cell.getCoordX() && y == cell.getCoordY())
                return true;
        }
        return false;
    }

    private boolean isValidBlock(String input, List<Block> blocks){
        List<String> blocksAvailable = new ArrayList<>();
        blocks.forEach(s -> blocksAvailable.add(s.toString()));
        for(String string : blocksAvailable) {
            if (string.equals(input))
                return true;
        }
        return false;
    }
}
