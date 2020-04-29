package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class ChooseInitialGodsResponse extends Message {

    private final List<GodData> payload;

    @JsonCreator
    public ChooseInitialGodsResponse(@JsonProperty("username") String username, @JsonProperty("payload") List<GodData> payload) {
        super(username, Content.CHOOSE_INITIAL_GODS);
        this.payload = payload;
    }

    public List<GodData> getPayload() {
        return payload;
    }

}
