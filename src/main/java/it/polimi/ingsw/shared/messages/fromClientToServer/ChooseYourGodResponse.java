package it.polimi.ingsw.shared.messages.fromClientToServer;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.GodData;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

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
