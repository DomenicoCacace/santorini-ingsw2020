package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

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

    @Override
    public boolean isBlocking() {
        return false;
    }

}
