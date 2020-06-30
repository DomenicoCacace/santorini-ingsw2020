package it.polimi.ingsw.server.network.exceptions;

public class RoomAlreadyExistingException extends Exception {

    @Override
    public String getMessage() {
        return "The room already exists";
    }
}
