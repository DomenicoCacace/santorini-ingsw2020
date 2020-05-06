package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.ReservedUsernames;

public class WinnerDeclaredResponse extends MessageFromServerToClient {

    private final String payload;

    @JsonCreator
    public WinnerDeclaredResponse(@JsonProperty("username") String payload) {
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
