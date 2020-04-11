package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.GameBoard;

public class PlayerMoveResponse extends MessageResponse {

    public final GameBoard payload;

    public PlayerMoveResponse(String outcome, String username, GameBoard payload) {
        super(outcome, username, Content.PLAYER_MOVE);
        if(outcome.equals("OK")){
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }

}
