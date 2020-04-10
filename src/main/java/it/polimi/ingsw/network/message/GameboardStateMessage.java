package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.GameBoard;

public class GameboardStateMessage extends MessageResponse {

    public final GameBoard payload;

    public GameboardStateMessage(String outcome, GameBoard payload) {
        super(outcome, "broadcast", Content.GAMEBOARD_STATE);
        this.payload = payload;
    }
}
