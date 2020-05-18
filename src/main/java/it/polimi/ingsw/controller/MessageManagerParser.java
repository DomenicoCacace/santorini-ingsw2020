package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseToReloadMatchResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.server.Lobby;

import java.util.List;

/**
 * Handles incoming messages from the client and their respective responses
 * <p>In particular, this class' objects handle all the messages regarding the actual game, from the choice of the gods
 * to the winner declaration.
 * <br>
 * It also provides the only contact point between the game controller and the <i>outside world</i>, via the
 * {@link #parseMessageFromServerToClient(Message)} method.
 * </p>
 */
public class MessageManagerParser implements ServerMessageManagerVisitor {

    private final Lobby lobby;
    private ServerController serverController;


    /**
     * Default constructor
     * <p>
     * Every {@link Lobby} is linked to a parser, which manages the incoming messages from the lobby's users;
     * The serverController has to be set separately, since it can be set up in different moments
     * </p>
     *
     * @param lobby the lobby associated to this parser
     */
    public MessageManagerParser(Lobby lobby) {
        this.lobby = lobby;
    }

    /**
     * Sets the parser's game controller
     *
     * @param serverController the game controller associated to this parser
     */
    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }
    //Client -> sends message request -> virtualClient -> lobby -> parseMessageFromClientToServer(MessageRequest) -> message parser will call methods of GameController and Lobby
    //Controller will call methods of model -> the model will return responses to the Controller -> Controller will pass the message to the Parser with parseMessageFromServerToClient
    //Parser will pass messages to the Lobby -> Lobby will pass messages to the virtualClient -> Client

    /**
     * Sends a message
     * <p>
     * Since the parser has no reference to the network interface, the message has to be sent to the lobby, which will take
     * care of sending the message, using the {@linkplain Lobby#sendMessage(String, Message)} method.
     * </p>
     *
     * @param message the message to send
     */
    public void parseMessageFromServerToClient(Message message) {
        lobby.sendMessage(message.getUsername(), message);
    }

    /**
     * Handles a {@linkplain ChooseToReloadMatchResponse} message
     *
     * @param message the message to handle
     * @see Lobby#reloadMatch(boolean)
     */
    @Override
    public void onMatchReloadResponse(ChooseToReloadMatchResponse message) {
        lobby.reloadMatch(message.wantToReload());
    }

    /**
     * Handles a {@linkplain ChooseInitialGodsResponse} message
     *
     * @param message the message to handle
     * @see Lobby#chooseGods(List)
     */
    @Override
    public void chooseInitialGods(ChooseInitialGodsResponse message) {
        lobby.chooseGods(message.getPayload());
    }

    /**
     * Handles a {@linkplain ChooseYourGodResponse} message
     *
     * @param message the message to handle
     * @see Lobby#assignGod(String, GodData)
     */
    @Override
    public void chooseGod(ChooseYourGodResponse message) {
        lobby.assignGod(message.getUsername(), message.getGod());
    }

    /**
     * Handles a {@linkplain ChooseStartingPlayerResponse} message
     *
     * @param message the message to handle
     * @see Lobby#selectStartingPlayer(String)
     */
    @Override
    public void chooseStartingPlayer(ChooseStartingPlayerResponse message) {
        lobby.selectStartingPlayer(message.getPayload());
    }

    /**
     * Handles a {@linkplain SelectWorkerRequest} message
     *
     * @param message the message to handle
     * @see ServerController#selectWorker(String, Worker)
     */
    @Override
    public void selectWorker(SelectWorkerRequest message) {
        serverController.selectWorker(message.getUsername(), message.getTargetWorker());
    }

    /**
     * Handles a {@linkplain WalkableCellsRequest} message
     *
     * @param message the message to handle
     * @see ServerController#obtainWalkableCells(String)
     */
    @Override
    public void walkableCells(WalkableCellsRequest message) {
        serverController.obtainWalkableCells(message.getUsername());
    }

    /**
     * Handles a {@linkplain BuildableCellsRequest} message
     *
     * @param message the message to handle
     * @see ServerController#obtainBuildableCells(String)
     */
    @Override
    public void buildableCells(BuildableCellsRequest message) {
        serverController.obtainBuildableCells(message.getUsername());
    }

    /**
     * Handles a {@linkplain SelectBuildingCellRequest} message
     *
     * @param message the message to handle
     * @see ServerController#selectBuildingCell(String, Cell)
     */
    @Override
    public void selectCellToBuild(SelectBuildingCellRequest message) {
        serverController.selectBuildingCell(message.getUsername(), message.getSelectedCell());
    }

    /**
     * Handles a {@linkplain PlayerMoveRequest} message
     *
     * @param message the message to handle
     * @see ServerController#handleMoveAction(String, MoveAction)
     */
    @Override
    public void managePlayerMove(PlayerMoveRequest message) {
        MoveAction moveAction = new MoveAction(message.getTargetWorker(), message.getTargetCell());
        serverController.handleMoveAction(message.getUsername(), moveAction);
    }

    /**
     * Handles a {@linkplain PlayerBuildRequest} message
     *
     * @param message the message to handle
     * @see ServerController#handleBuildAction(String, BuildAction)
     */
    @Override
    public void managePlayerBuild(PlayerBuildRequest message) {
        BuildAction buildAction = new BuildAction(message.getTargetWorker(), message.getTargetCell(), message.getTargetBlock());
        serverController.handleBuildAction(message.getUsername(), buildAction);
    }

    /**
     * Handles a {@linkplain AddWorkerRequest} message
     *
     * @param message the message to handle
     * @see ServerController#addWorker(String, Cell)
     */
    @Override
    public void addWorkerOnBoard(AddWorkerRequest message) {
        serverController.addWorker(message.getUsername(), message.getTargetCell());
    }

    /**
     * Handles a {@linkplain EndTurnRequest} message
     *
     * @param message the message to handle
     * @see ServerController#onTurnEnd(String)
     */
    @Override
    public void endTurn(EndTurnRequest message) {
        serverController.passTurn(message.getUsername());
    }

    /**
     * Invoked in case the message received has a Visitor method not implemented in this parser. In this case, it has been
     * decided to ignore the message and literally take no action, leading the client to doom.
     *
     * @param message the message to discard
     */
    @Override
    public void cannotHandleMessage(Message message) {
        //do nothing
    }
}
