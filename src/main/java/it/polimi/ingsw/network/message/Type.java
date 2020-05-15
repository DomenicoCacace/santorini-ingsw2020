package it.polimi.ingsw.network.message;

public enum Type {
    // general purpose
    OK,
    ERROR_GENERAL,
    NOTIFY,
    CLIENT_REQUEST,
    SERVER_REQUEST,

    ADD_WORKER,

    ADDING_FAILED,
    PLAYER_REMOVED,
    NOT_YOUR_WORKER,
    NO_WORKER_SELECTED,
    ILLEGAL_MOVEMENT,
    ILLEGAL_BUILD,
    CANNOT_END_TURN,
    INVALID_NAME,
    SERVER_FULL,
    LOBBY_FULL,
    INVALID_GOD_CHOICE,
    NO_LOBBY_AVAILABLE
}
