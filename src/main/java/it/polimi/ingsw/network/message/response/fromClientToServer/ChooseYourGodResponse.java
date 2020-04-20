package it.polimi.ingsw.network.message.response.fromClientToServer;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;

public class ChooseYourGodResponse extends Message {
    public final GodData god;

    @JsonCreator
    public ChooseYourGodResponse(@JsonProperty("username") String username, @JsonProperty("god") GodData god) {
        super(username, Content.CHOOSE_GOD);
        this.god = god;
    }

}
