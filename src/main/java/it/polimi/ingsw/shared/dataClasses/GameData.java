package it.polimi.ingsw.shared.dataClasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.server.model.Game;

import java.util.List;

/**
 * A data class for {@linkplain Game}
 * <p>
 * It can be useful to send game information over the network, without any reference to the actual game object.
 * <br>
 * Its attributes are data classes too.
 */
public class GameData {
    private final List<Cell> board;
    private final List<PlayerData> players;
    private final TurnData currentTurn;

    /**
     * Default constructor
     *
     * @param board       the gameBoard
     * @param players     the list of players
     * @param currentTurn the current turn information
     */
    @JsonCreator
    public GameData(@JsonProperty("board") List<Cell> board, @JsonProperty("players") List<PlayerData> players, @JsonProperty("currentTurn") TurnData currentTurn) {
        this.board = board;
        this.players = players;
        this.currentTurn = currentTurn;
    }

    /**
     * <i>board</i> getter
     *
     * @return the gameBoard
     */
    public List<Cell> getBoard() {
        return board;
    }

    /**
     * <i>players</i> getter
     *
     * @return the list of players
     */
    public List<PlayerData> getPlayers() {
        return players;
    }

    /**
     * <i>currentTurn</i> getter
     *
     * @return the current turn
     */
    public TurnData getCurrentTurn() {
        return currentTurn;
    }
}
