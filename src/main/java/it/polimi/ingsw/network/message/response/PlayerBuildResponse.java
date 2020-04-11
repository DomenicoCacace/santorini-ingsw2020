package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.GameBoard;

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
