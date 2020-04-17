package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.message.response.MessageResponse;

public class PlayerRemovedResponse extends MessageResponse {

    public final Game payload;

    public PlayerRemovedResponse(String outcome, Game payload){
        super(outcome, "broadcast", Content.PLAYER_REMOVED);
        this.payload = payload;
    }
}
