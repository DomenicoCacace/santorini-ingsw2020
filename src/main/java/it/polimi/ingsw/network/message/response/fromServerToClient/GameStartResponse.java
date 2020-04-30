package it.polimi.ingsw.network.message.response.fromServerToClient;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GameData;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;


public class GameStartResponse extends MessageFromServerToClient {
    private final GameData payload;
    private final String outcome;

    @JsonCreator
    public GameStartResponse(@JsonProperty("outcome") String outcome, @JsonProperty("payload") GameData payload) {
        super("broadcast", Content.GAME_START);
        this.outcome = outcome;
        if (outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }

    public GameData getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onGameStart(this);
    }
}
