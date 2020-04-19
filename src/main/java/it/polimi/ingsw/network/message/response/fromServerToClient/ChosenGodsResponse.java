package it.polimi.ingsw.network.message.response.fromServerToClient;


import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;
import java.util.List;

public class ChosenGodsResponse extends Message {

    public final List<GodData> payload;
    public final String outcome;

    public ChosenGodsResponse(String outcome, String username, List<GodData> payload) {
        super(username, Content.CHOSEN_GODS);
        this.outcome = outcome;
        if (outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }
}

