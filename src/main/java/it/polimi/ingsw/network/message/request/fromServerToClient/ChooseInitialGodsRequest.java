package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.request.MessageRequest;

import java.util.List;

public class ChooseInitialGodsRequest extends MessageRequest {

    public final List<God> gods;

    public ChooseInitialGodsRequest(String username, List<God> gods){
        super(username, Content.CHOOSE_INITIAL_GODS);
        this.gods = gods;
    }
}
