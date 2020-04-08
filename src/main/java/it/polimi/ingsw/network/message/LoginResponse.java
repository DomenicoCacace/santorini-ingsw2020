package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.GameBoard;

public class LoginResponse extends Message {
    public final Type response;
    public final GameBoard payload;

    public LoginResponse(boolean ok, GameBoard payload) {
        super("admin", Content.LOGIN);
        if(ok){
            response = Type.OK;
            this.payload = payload;
        } else {
            response = Type.FAILURE;
            this.payload = null;
        }
    }
}
