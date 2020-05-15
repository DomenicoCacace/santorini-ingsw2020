package it.polimi.ingsw.network.message.response.fromClientToServer;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

public class ChooseYourGodResponse extends MessageFromClientToServer {
    private final GodData god;

    @JsonCreator
    public ChooseYourGodResponse(@JsonProperty("username") String username, @JsonProperty("god") GodData god) {
        super(username, Type.NOTIFY);
        this.god = god;
    }

    public GodData getGod() {
        return god;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.chooseGod(this);
    }
}
