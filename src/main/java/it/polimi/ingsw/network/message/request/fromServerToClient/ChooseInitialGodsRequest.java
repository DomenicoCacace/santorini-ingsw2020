package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class ChooseInitialGodsRequest extends Message {

    public final List<GodData> gods;

    @JsonCreator
    public ChooseInitialGodsRequest(@JsonProperty("username") String username,@JsonProperty("gods") List<GodData> gods) {
        super(username, Content.CHOOSE_INITIAL_GODS);
        this.gods = gods;
    }
}
