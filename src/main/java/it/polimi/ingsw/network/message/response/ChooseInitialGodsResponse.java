package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.God;

import java.util.List;

public class ChooseInitialGodsResponse extends MessageResponse {

    public final List<God> payload;

    public ChooseInitialGodsResponse (String outcome, String username, List<God> payload) {
        super(outcome, username, Content.CHOOSE_INITIAL_GODS);
        if(outcome.equals("OK")){
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }
}
