package it.polimi.ingsw.network.message.response.fromServerToClient;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GameData;
import it.polimi.ingsw.network.message.Message;


public class GameStartResponse extends Message {
    public final GameData payload;
    public final String outcome;

    @JsonCreator
    public GameStartResponse(@JsonProperty("outcome") String outcome,@JsonProperty("payload") GameData payload) {
        super("broadcast", Content.GAME_START);
        this.outcome = outcome;
        if (outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }
}
