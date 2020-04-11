package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.GameBoard;

public class GameStartMessage extends MessageResponse {

    public final GameBoard payload;

    /**
     * Constructor
     * @param payload  Initial state of the gameboard
     */

    public GameStartMessage(String outcome, GameBoard payload) {
        super(outcome, "broadcast", Content.GAME_START);
        this.payload = payload;
    }
}
