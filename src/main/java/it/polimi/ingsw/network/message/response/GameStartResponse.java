package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.Game;


public class GameStartResponse extends MessageResponse {

    public final Game payload;

    public GameStartResponse(String outcome, Game payload) {
        super(outcome, "broadcast", Content.GAME_START);
        this.payload = payload;
    }
}
