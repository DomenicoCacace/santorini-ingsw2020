package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.GameBoard;

public class GameStartMessage extends Message {
    public final GameBoard payload;
    public final int timerValue;

    /**
     * Constructor
     * @param timerValue Initial timer value
     * @param payload  Initial state of the gameboard
     */
    public GameStartMessage(GameBoard payload, int timerValue) {
        super("admin", Content.GAME_START);
        this.payload = payload;
        this.timerValue = timerValue;
    }
}
