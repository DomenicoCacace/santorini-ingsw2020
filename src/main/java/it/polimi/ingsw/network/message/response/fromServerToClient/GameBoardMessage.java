package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

import java.util.List;

public class GameBoardMessage extends MessageFromServerToClient {

    private final List<Cell> gameBoard;

    @JsonCreator
    public GameBoardMessage(@JsonProperty("username") String username,@JsonProperty("gameBoard") List<Cell> gameBoard) {
        super(username, Content.GAMEBOARD);
        this.gameBoard = gameBoard;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onGameBoardUpdate(this);
    }

    public List<Cell> getGameBoard() {
        return gameBoard;
    }
}
