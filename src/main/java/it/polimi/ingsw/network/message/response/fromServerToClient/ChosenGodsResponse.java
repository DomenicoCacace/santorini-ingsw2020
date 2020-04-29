package it.polimi.ingsw.network.message.response.fromServerToClient;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class ChosenGodsResponse extends Message {

    private final List<GodData> payload;
    private final String outcome;

    @JsonCreator
    public ChosenGodsResponse(@JsonProperty("outcome") String outcome, @JsonProperty("username") String username, @JsonProperty("payload") List<GodData> payload) {
        super(username, Content.CHOSEN_GODS);
        this.outcome = outcome;
        if (outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }

    public List<GodData> getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }
}

