package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.God;

@Deprecated
public class AssignGodResponse extends MessageResponse {

    public final God payload;

    public AssignGodResponse (String outcome, String username, God payload) {
        super(outcome, username, Content.ASSIGN_GOD);
        if(outcome.equals("OK")){
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }
}
