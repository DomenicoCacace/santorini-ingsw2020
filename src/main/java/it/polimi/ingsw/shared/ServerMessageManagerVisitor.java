package it.polimi.ingsw.shared;

import it.polimi.ingsw.shared.messages.Message;
import it.polimi.ingsw.shared.messages.fromClientToServer.*;

/**
 * Visitor interface for messages travelling from the client(s) to the server
 * <p>
 * Since in the server the messages can be handled in more than one point, the Visitors are declared as default methods
 * running the {@linkplain #cannotHandleMessage(Message)} method, which implementation differs based on the class
 * which has to handle the messages
 */
public interface ServerMessageManagerVisitor {

    /**
     * Visitor for {@linkplain LoginRequest}
     *
     * @param message the message to visit
     */
    default void login(LoginRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain JoinLobbyRequest}
     *
     * @param message the message to visit
     */
    default void joinLobby(JoinLobbyRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain CreateLobbyRequest}
     *
     * @param message the message to visit
     */
    default void createLobby(CreateLobbyRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain ChooseInitialGodsResponse}
     *
     * @param message the message to visit
     */
    default void chooseInitialGods(ChooseInitialGodsResponse message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain ChooseYourGodResponse}
     *
     * @param message the message to visit
     */
    default void chooseGod(ChooseYourGodResponse message) {
        cannotHandleMessage(message);
    }


    default void chooseStartingPlayer(ChooseStartingPlayerResponse message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain ChooseToReloadMatchResponse}
     *
     * @param message the message to visit
     */
    default void onMatchReloadResponse(ChooseToReloadMatchResponse message) {
        cannotHandleMessage(message);
    }

    // strictly game related, handled by the MessageManagerParser

    /**
     * Visitor for {@linkplain AddWorkerRequest}
     *
     * @param message the message to visit
     */
    default void addWorkerOnBoard(AddWorkerRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain SelectWorkerRequest}
     *
     * @param message the message to visit
     */
    default void selectWorker(SelectWorkerRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain WalkableCellsRequest}
     *
     * @param message the message to visit
     */
    default void walkableCells(WalkableCellsRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain BuildableCellsRequest}
     *
     * @param message the message to visit
     */
    default void buildableCells(BuildableCellsRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain PlayerMoveRequest}
     *
     * @param message the message to visit
     */
    default void managePlayerMove(PlayerMoveRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain SelectBuildingCellRequest}
     *
     * @param message the message to visit
     */
    default void selectCellToBuild(SelectBuildingCellRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain PlayerBuildRequest}
     *
     * @param message the message to visit
     */
    default void managePlayerBuild(PlayerBuildRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Visitor for {@linkplain EndTurnRequest}
     *
     * @param message the message to visit
     */
    default void endTurn(EndTurnRequest message) {
        cannotHandleMessage(message);
    }

    /**
     * Handles messages not supposed to be visited in the class it is implemented to
     *
     * @param message the message to handle
     */
    void cannotHandleMessage(Message message);
}

