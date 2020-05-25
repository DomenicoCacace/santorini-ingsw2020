package it.polimi.ingsw.network.message.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class GameBoardUpdate extends MessageFromServerToClient {

    private final List<Cell> gameBoard;

    @JsonCreator
    public GameBoardUpdate(@JsonProperty("username") String username, @JsonProperty("gameBoard") List<Cell> gameBoard) {
        super(username, Type.NOTIFY);
        this.gameBoard = gameBoard;
    }

    public List<Cell> getGameBoard() {
        return gameBoard;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onGameBoardUpdate(this);
    }

}
