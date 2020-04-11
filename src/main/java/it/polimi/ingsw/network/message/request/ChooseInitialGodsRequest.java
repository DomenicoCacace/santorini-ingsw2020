package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.God;

import java.util.List;

public class ChooseInitialGodsRequest extends MessageRequest {

    public ChooseInitialGodsRequest(String username, List<God> gods){
        super(username, Content.CHOOSE_INITIAL_GODS);
    }
}
