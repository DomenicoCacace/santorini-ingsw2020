package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

public class ChooseToReloadMatchResponse extends MessageFromClientToServer {

    private final boolean reload;

    @JsonCreator
    public ChooseToReloadMatchResponse(@JsonProperty("username") String username, @JsonProperty("reload") boolean reload) {
        super(username, Type.NOTIFY);
        this.reload = reload;
    }

    @JsonGetter("reload")
    public boolean wantToReload() {
        return reload;
    }


    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.onMatchReloadResponse(this);

    }
}
