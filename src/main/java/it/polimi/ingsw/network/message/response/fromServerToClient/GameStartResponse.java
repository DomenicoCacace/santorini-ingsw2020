package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.message.Message;



public class GameStartResponse extends Message {
    public final Game payload;
    public final String outcome;

    public GameStartResponse(String outcome, Game payload) {
        super("broadcast", Content.GAME_START);
        this.outcome = outcome;
        if(outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }
}
