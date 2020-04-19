package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class ChooseInitialGodsRequest extends Message {

    public final List<GodData> gods;

    public ChooseInitialGodsRequest(String username, List<GodData> gods){
        super(username, Content.CHOOSE_INITIAL_GODS);
        this.gods = gods;
    }
}
