package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.message.response.MessageResponse;


public class GameStartResponse extends MessageResponse {

    public final Game payload;

    public GameStartResponse(String outcome, Game payload) {
        super(outcome, "broadcast", Content.GAME_START);
        this.payload = payload;
    }
}
