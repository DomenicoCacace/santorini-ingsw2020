package it.polimi.ingsw.server.network.exceptions;

public class RoomFullException extends Exception {

    @Override
    public String getMessage() {
        return "The room is full";
    }

}
