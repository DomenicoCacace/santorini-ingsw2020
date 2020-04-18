package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.Message;

@Deprecated
public class AssignGodResponse extends Message {

    public final God payload;
    public final String outcome;

    public AssignGodResponse (String outcome, String username, God payload) {
        super(username, Content.ASSIGN_GOD);
        this.outcome = outcome;
        if(outcome.equals("OK")){
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }
}
