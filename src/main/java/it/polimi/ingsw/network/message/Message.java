package it.polimi.ingsw.network.message;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseStartingPlayerRequest;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;

/**
 * Abstract message
 * <p>
 *     Each message has, in any case, two attributes:
 *     <ul>
 *         <li>username: a String, containing the sender's username</li>
 *         <li>type: an Enum value, which indicates what kind of payload to expect</li>
 *     </ul>
 * </p>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginResponse.class, name = "Login"),
        @JsonSubTypes.Type(value = UserJoinedLobbyEvent.class, name = "JoinLobby"),
        @JsonSubTypes.Type(value = LobbyCreatedEvent.class, name = "CreateLobby"),
        @JsonSubTypes.Type(value = WorkerAddedEvent.class, name = "AddWorker"),
        @JsonSubTypes.Type(value = BuildableCellsResponse.class, name = "BuildableCells"),
        @JsonSubTypes.Type(value = EndTurnEvent.class, name = "EndTurn"),
        @JsonSubTypes.Type(value = PlayerBuildEvent.class, name = "PlayerBuild"),
        @JsonSubTypes.Type(value = PlayerMoveEvent.class, name = "PlayerMove"),
        @JsonSubTypes.Type(value = WorkerSelectedEvent.class, name = "SelectWorker"),
        @JsonSubTypes.Type(value = WalkableCellsResponse.class, name = "WalkableCells"),
        @JsonSubTypes.Type(value = ChooseInitialGodsResponse.class, name = "ChooseInitialGodsResponse"),
        @JsonSubTypes.Type(value = ChosenGodsEvent.class, name = "ChooseYourGodResponse"),
        @JsonSubTypes.Type(value = ChooseStartingPlayerRequest.class, name = "ChooseStartingPlayer"),
        @JsonSubTypes.Type(value = ChooseStartingPlayerResponse.class, name = "ChooseStartingPlayer"),
        @JsonSubTypes.Type(value = GameStartEvent.class, name = "GameStarted"),
        @JsonSubTypes.Type(value = PlayerRemovedEvent.class, name = "PlayerRemoved"),
        @JsonSubTypes.Type(value = WinnerDeclaredEvent.class, name = "WinnerDeclared"),
        @JsonSubTypes.Type(value = WorkerAddedEvent.class, name = "AddWorker"),
        @JsonSubTypes.Type(value = BuildableCellsResponse.class, name = "BuildableCells"),
        @JsonSubTypes.Type(value = PossibleBuildingBlock.class, name = "SelectBuildingCell"),
        @JsonSubTypes.Type(value = EndTurnEvent.class, name = "EndTurn"),
        @JsonSubTypes.Type(value = LoginResponse.class, name = "Login"),
        @JsonSubTypes.Type(value = PlayerBuildEvent.class, name = "PlayerBuild"),
        @JsonSubTypes.Type(value = PlayerMoveEvent.class, name = "PlayerMove"),
        @JsonSubTypes.Type(value = WorkerSelectedEvent.class, name = "SelectWorker"),
        @JsonSubTypes.Type(value = WalkableCellsResponse.class, name = "WalkableCells"),
        @JsonSubTypes.Type(value = ChooseInitialGodsResponse.class, name = "ChooseInitialGodsResponse"),
        @JsonSubTypes.Type(value = ChosenGodsEvent.class, name = "ChooseYourGodResponse"),
        @JsonSubTypes.Type(value = GameStartEvent.class, name = "GameStarted"),
        @JsonSubTypes.Type(value = PlayerRemovedEvent.class, name = "PlayerRemoved"),
        @JsonSubTypes.Type(value = WinnerDeclaredEvent.class, name = "WinnerDeclared"),
})
public abstract class Message {
    private final String username;
    private final Type type;

    /**
     * Message constructor
     * @param username the sender's username
     * @param type the message type
     */
    @JsonCreator
    public Message(@JsonProperty("username") String username, @JsonProperty("type") Type type) {
        this.username = username;
        this.type = type;
    }

    /**
     * <i>username</i> getter
     * @return the sender's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * <i>type</i> getter
     * @return the message type
     */
    public Type getType() {
        return type;
    }
}
