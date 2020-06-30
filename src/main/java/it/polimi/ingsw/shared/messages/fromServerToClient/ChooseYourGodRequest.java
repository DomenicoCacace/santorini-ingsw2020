package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.GodData;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class ChooseYourGodRequest extends MessageFromServerToClient {

    private final List<GodData> gods;

    @JsonCreator
    public ChooseYourGodRequest(@JsonProperty("username") String username, @JsonProperty("gods") List<GodData> gods) {
        super(username, Type.SERVER_REQUEST);
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
