package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.GameBoard;

public class GameboardStateMessage  extends Message{
    public final GameBoard payload;

    public GameboardStateMessage(GameBoard gameBoard) {
        super("admin", Content.GAMEBOARD_STATE);
        payload = gameBoard;
    }
}
