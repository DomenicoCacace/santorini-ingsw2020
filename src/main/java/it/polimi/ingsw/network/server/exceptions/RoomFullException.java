package it.polimi.ingsw.network.server.exceptions;

public class RoomFullException extends Exception {

    @Override
    public String getMessage() {
        return "The room is full";
    }

}
