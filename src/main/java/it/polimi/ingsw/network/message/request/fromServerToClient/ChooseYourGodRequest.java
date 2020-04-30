package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

import java.util.List;

public class ChooseYourGodRequest extends MessageFromServerToClient {

    private final List<GodData> gods;

    @JsonCreator
    public ChooseYourGodRequest(@JsonProperty("username") String username, @JsonProperty("gods") List<GodData> gods) {
        super(username, Content.CHOOSE_GOD);
        this.gods = gods;
    }

    public List<GodData> getGods() {
        return gods;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.chooseYourGod(this);
    }
}
