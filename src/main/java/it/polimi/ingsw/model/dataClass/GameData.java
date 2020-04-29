package it.polimi.ingsw.model.dataClass;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;

import java.util.List;

public class GameData {
    private final List<Cell> board;
    private final List<PlayerData> players;
    private final TurnData currentTurn;

    @JsonCreator
    public GameData(@JsonProperty("board") List<Cell> board, @JsonProperty("players") List<PlayerData> players, @JsonProperty("currentTurn") TurnData currentTurn) {
        this.board = board;
        this.players = players;
        this.currentTurn = currentTurn;
    }

    public List<Cell> getBoard() {
        return board;
    }

    public List<PlayerData> getPlayers() {
        return players;
    }

    public TurnData getCurrentTurn() {
        return currentTurn;
    }
}
