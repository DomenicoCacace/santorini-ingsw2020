package it.polimi.ingsw.network.message;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseStartingPlayerRequest;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseNumberOfPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddWorkerResponse.class, name = "AddWorker"),
        @JsonSubTypes.Type(value = BuildableCellsResponse.class, name = "BuildableCells"),
        @JsonSubTypes.Type(value = EndTurnResponse.class, name = "EndTurn"),
        @JsonSubTypes.Type(value = LoginResponse.class, name = "Login"),
        @JsonSubTypes.Type(value = PlayerBuildResponse.class, name = "PlayerBuild"),
        @JsonSubTypes.Type(value = PlayerMoveResponse.class, name = "PlayerMove"),
        @JsonSubTypes.Type(value = SelectWorkerResponse.class, name = "SelectWorker"),
        @JsonSubTypes.Type(value = WalkableCellsResponse.class, name = "WalkableCells"),
        @JsonSubTypes.Type(value = ChooseInitialGodsResponse.class, name = "ChooseInitialGodsResponse"),
        @JsonSubTypes.Type(value = ChooseNumberOfPlayerResponse.class, name = "ChooseNumberOfPlayers"),
        @JsonSubTypes.Type(value = ChosenGodsResponse.class, name = "ChooseYourGodResponse"),
        @JsonSubTypes.Type(value = ChooseStartingPlayerRequest.class, name = "ChooseStartingPlayer"),
        @JsonSubTypes.Type(value = ChooseStartingPlayerResponse.class, name = "ChooseStartingPlayer"),
        @JsonSubTypes.Type(value = GameStartResponse.class, name = "GameStarted"),
        @JsonSubTypes.Type(value = PlayerRemovedResponse.class, name = "PlayerRemoved"),
        @JsonSubTypes.Type(value = WinnerDeclaredResponse.class, name = "WinnerDeclared"),
        @JsonSubTypes.Type(value = AddWorkerResponse.class, name = "AddWorker"),
        @JsonSubTypes.Type(value = BuildableCellsResponse.class, name = "BuildableCells"),
        @JsonSubTypes.Type(value = SelectBuildingCellResponse.class, name = "SelectBuildingCell"),
        @JsonSubTypes.Type(value = EndTurnResponse.class, name = "EndTurn"),
        @JsonSubTypes.Type(value = LoginResponse.class, name = "Login"),
        @JsonSubTypes.Type(value = PlayerBuildResponse.class, name = "PlayerBuild"),
        @JsonSubTypes.Type(value = PlayerMoveResponse.class, name = "PlayerMove"),
        @JsonSubTypes.Type(value = SelectWorkerResponse.class, name = "SelectWorker"),
        @JsonSubTypes.Type(value = WalkableCellsResponse.class, name = "WalkableCells"),
        @JsonSubTypes.Type(value = ChooseInitialGodsResponse.class, name = "ChooseInitialGodsResponse"),
        @JsonSubTypes.Type(value = ChooseNumberOfPlayerResponse.class, name = "ChooseNumberOfPlayers"),
        @JsonSubTypes.Type(value = ChosenGodsResponse.class, name = "ChooseYourGodResponse"),
        @JsonSubTypes.Type(value = GameStartResponse.class, name = "GameStarted"),
        @JsonSubTypes.Type(value = PlayerRemovedResponse.class, name = "PlayerRemoved"),
        @JsonSubTypes.Type(value = WinnerDeclaredResponse.class, name = "WinnerDeclared"),
})
public abstract class Message {
    private final String username;
    private final Content content;


    @JsonCreator
    public Message(@JsonProperty("username") String username, @JsonProperty("content") Content content) {
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public Content getContent() {
        return content;
    }

    public enum Content { //FIXME: We use content only to check if the message is a login or choose_player_number, we don't need all the others
        LOGIN, PLAYER_MOVE, PLAYER_BUILD, SELECT_BUILDING_CELL, RELOAD_MATCH,
        END_TURN, ADD_WORKER, CHOOSE_INITIAL_GODS, STARTING_PLAYER, WINNER_DECLARED,
        PLAYER_REMOVED, CHOOSE_PLAYER_NUMBER, CHOOSE_GOD, SELECT_WORKER, GAMEBOARD,
        WALKABLE_CELLS, BUILDABLE_CELLS, CHOSEN_GODS, GAME_START, WORKER_POSITION
    }
}
