package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.request.MessageRequest;


import java.util.List;

public class ChooseYourGodRequest extends MessageRequest {
    public final List<God> gods;
    public ChooseYourGodRequest(String username, List<God> gods){
        super(username, Content.CHOOSE_GOD);
        this.gods = gods;
    }
}
