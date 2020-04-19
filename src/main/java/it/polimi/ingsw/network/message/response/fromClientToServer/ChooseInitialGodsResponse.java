package it.polimi.ingsw.network.message.response.fromClientToServer;

import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class ChooseInitialGodsResponse extends Message {

    public final List<GodData> payload;
    public final String outcome;

    public ChooseInitialGodsResponse(String outcome, String username, List<GodData> payload) {
        super(username, Content.CHOOSE_INITIAL_GODS);
        this.outcome = outcome;
        if (outcome.equals("OK")) {
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }
}
