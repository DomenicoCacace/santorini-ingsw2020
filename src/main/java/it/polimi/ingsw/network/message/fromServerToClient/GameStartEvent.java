package it.polimi.ingsw.network.message.fromServerToClient;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GameData;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;


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
