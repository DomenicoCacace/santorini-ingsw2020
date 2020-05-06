package it.polimi.ingsw.network.server.exceptions;

public class RoomAlreadyExistingException extends Exception{

    @Override
    public String getMessage() {
        return "The room already exists";
    }
}
