package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.message.Message;

public class PlayerRemovedResponse extends Message {

    public final Game payload;
    public final String outcome;

    public PlayerRemovedResponse(String outcome, Game payload){
        super("broadcast", Content.PLAYER_REMOVED);
        this.outcome = outcome;
        this.payload = payload;
    }
}
