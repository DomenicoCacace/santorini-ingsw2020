package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;
import java.util.Map;

public class MovedToWaitingRoomResponse extends MessageFromServerToClient {
    private final Map<String, List<String>> availableLobbies;
    private final String disconnectedUser;

    /**
     * Message constructor
     *
     * @param username         the sender's username
     * @param type             the message type
     * @param availableLobbies a list containing the lobbies available
     * @param disconnectedUser the user which has been disconnected
     */
    @JsonCreator
    public MovedToWaitingRoomResponse(@JsonProperty("username") String username, @JsonProperty("type") Type type,
                                      @JsonProperty("available") Map<String, List<String>> availableLobbies, @JsonProperty("disconnectedUser") String disconnectedUser) {
        super(username, type);
        this.availableLobbies = availableLobbies;
        this.disconnectedUser = disconnectedUser;
    }

    public Map<String, List<String>> getAvailableLobbies() {
        return availableLobbies;
    }

    public String getDisconnectedUser() {
        return disconnectedUser;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onMovedToWaitingRoom(this);
    }

}
