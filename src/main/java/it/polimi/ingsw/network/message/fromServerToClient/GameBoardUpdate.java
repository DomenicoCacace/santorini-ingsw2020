package it.polimi.ingsw.network.message.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class GameBoardUpdate extends MessageFromServerToClient {

    private final List<Cell> gameBoard;
    private final List<PlayerData> players;

    @JsonCreator
    public GameBoardUpdate(@JsonProperty("username") String username, @JsonProperty("gameBoard") List<Cell> gameBoard, @JsonProperty("players") List<PlayerData> players) {
        super(username, Type.NOTIFY);
        this.gameBoard = gameBoard;
        this.players = players;
    }

    public List<Cell> getGameBoard() {
        return gameBoard;
    }

    public List<PlayerData> getPlayers() {
        return players;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onGameBoardUpdate(this);
    }

}
