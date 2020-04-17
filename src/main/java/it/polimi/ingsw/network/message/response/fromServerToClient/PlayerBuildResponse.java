package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.GameBoard;
import it.polimi.ingsw.network.message.response.MessageResponse;

public class PlayerBuildResponse extends MessageResponse {

    public final GameBoard payload;

    public PlayerBuildResponse(String outcome, String username, GameBoard payload) {
        super(outcome, username, Content.PLAYER_BUILD);
        if(outcome.equals("OK")){
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }
}
