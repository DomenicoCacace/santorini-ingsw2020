package it.polimi.ingsw.shared.messages.fromServerToClient;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.ReservedUsernames;
import it.polimi.ingsw.shared.dataClasses.GameData;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;


public class GameStartEvent extends MessageFromServerToClient {

    private final GameData payload;

    @JsonCreator
    public GameStartEvent(@JsonProperty("type") Type type, @JsonProperty("payload") GameData payload) {
        super(ReservedUsernames.BROADCAST.toString(), type);
        if (type.equals(Type.OK))
            this.payload = payload;
        else
            this.payload = null;
    }

    public GameData getPayload() {
        return payload;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onGameStart(this);
    }

}
