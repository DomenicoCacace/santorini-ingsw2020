package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.ReservedUsernames;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

public class WinnerDeclaredEvent extends MessageFromServerToClient {

    private final String payload;

    @JsonCreator
    public WinnerDeclaredEvent(@JsonProperty("username") String payload) {
        super(ReservedUsernames.BROADCAST.toString(), Type.NOTIFY);
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onWinnerDeclared(this);
    }

}
