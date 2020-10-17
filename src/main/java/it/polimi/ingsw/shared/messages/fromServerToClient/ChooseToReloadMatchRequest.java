package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

public class ChooseToReloadMatchRequest extends MessageFromServerToClient {

    @JsonCreator
    public ChooseToReloadMatchRequest(@JsonProperty("username") String username) {
        super(username, Type.SERVER_REQUEST);
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.chooseToReloadMatch(this);
    }

}
