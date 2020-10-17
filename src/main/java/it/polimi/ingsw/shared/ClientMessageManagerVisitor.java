package it.polimi.ingsw.shared;

import it.polimi.ingsw.shared.messages.fromServerToClient.*;

/**
 * Visitor interface for messages travelling from the server to the client
 */
public interface ClientMessageManagerVisitor {

    /**
     * Visitor for {@linkplain LoginResponse}
     *
     * @param message the message to visit
     */
    void onLogin(LoginResponse message);

    /**
     * Visitor for {@linkplain UserJoinedLobbyEvent}
     *
     * @param message the message to visit
     */
    void joinLobby(UserJoinedLobbyEvent message);

    /**
     * Visitor for {@linkplain LobbyCreatedEvent}
     *
     * @param message the message to visit
     */
    void createLobby(LobbyCreatedEvent message);

    /**
     * Visitor for {@linkplain ChooseToReloadMatchRequest}
     *
     * @param message the message to visit
     */
    void chooseToReloadMatch(ChooseToReloadMatchRequest message);

    /**
     * Visitor for {@linkplain ChooseInitialGodsRequest}
     *
     * @param message the message to visit
     */
    void chooseInitialGods(ChooseInitialGodsRequest message);

    /**
     * Visitor for {@linkplain ChosenGodsEvent}
     *
     * @param message the message to visit
     */
    void onGodChosen(ChosenGodsEvent message);

    /**
     * Visitor for {@linkplain ChooseYourGodRequest}
     *
     * @param message the message to visit
     */
    void chooseYourGod(ChooseYourGodRequest message);

    /**
     * Visitor for {@linkplain ChooseStartingPlayerRequest}
     *
     * @param message the message to visit
     */
    void chooseStartingPlayer(ChooseStartingPlayerRequest message);

    /**
     * Visitor for {@linkplain GameStartEvent}
     *
     * @param message the message to visit
     */
    void onGameStart(GameStartEvent message);

    /**
     * Visitor for {@linkplain GameBoardUpdate}
     *
     * @param message the message to visit
     */
    void onGameBoardUpdate(GameBoardUpdate message);

    /**
     * Visitor for {@linkplain WorkerSelectedResponse}
     *
     * @param message the message to visit
     */
    void onWorkerSelected(WorkerSelectedResponse message);

    /**
     * Visitor for {@linkplain WorkerSelectedResponse}
     *
     * @param message the message to visit
     */
    void onWalkableCellsReceived(WalkableCellsResponse message);

    /**
     * Visitor for {@linkplain BuildableCellsResponse}
     *
     * @param message the message to visit
     */
    void onBuildableCellsReceived(BuildableCellsResponse message);

    /**
     * Visitor for {@linkplain PossibleBuildingBlockResponse}
     *
     * @param message the message to visit
     */
    void onBuildingCellSelected(PossibleBuildingBlockResponse message);

    /**
     * Visitor for {@linkplain PlayerMoveEvent}
     *
     * @param message the message to visit
     */
    void onPlayerMove(PlayerMoveEvent message);

    /**
     * Visitor for {@linkplain PlayerBuildEvent}
     *
     * @param message the message to visit
     */
    void onPlayerBuild(PlayerBuildEvent message);

    /**
     * Visitor for {@linkplain EndTurnEvent}
     *
     * @param message the message to visit
     */
    void onTurnEnd(EndTurnEvent message);

    /**
     * Visitor for {@linkplain ChooseWorkerPositionRequest}
     *
     * @param message the message to visit
     */
    void chooseYourWorkerPosition(ChooseWorkerPositionRequest message);

    /**
     * Visitor for {@linkplain WorkerAddedEvent}
     *
     * @param message the message to visit
     */
    void onWorkerAdd(WorkerAddedEvent message);

    /**
     * Visitor for {@linkplain WinnerDeclaredEvent}
     *
     * @param message the message to visit
     */
    void onWinnerDeclared(WinnerDeclaredEvent message);

    /**
     * Visitor for {@linkplain PlayerRemovedEvent}
     *
     * @param message the message to visit
     */
    void onPlayerRemoved(PlayerRemovedEvent message);

    /**
     * Visitor for {@linkplain MovedToWaitingRoomResponse}
     *
     * @param message the message to visit
     */
    void onMovedToWaitingRoom(MovedToWaitingRoomResponse message);
}
