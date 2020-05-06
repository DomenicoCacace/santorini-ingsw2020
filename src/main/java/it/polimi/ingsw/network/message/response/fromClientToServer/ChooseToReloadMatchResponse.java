package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

public class ChooseToReloadMatchResponse extends MessageFromClientToServer {

    private final boolean reload;

    @JsonCreator
    public ChooseToReloadMatchResponse(@JsonProperty("username") String username,@JsonProperty("reload") boolean reload) {
        super(username, Type.NOTIFY);
        this.reload=reload;
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
