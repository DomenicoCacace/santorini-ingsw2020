package it.polimi.ingsw.network.message.request.fromServerToClient;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.Message;


import java.util.List;

public class ChooseYourGodRequest extends Message {
    public final List<GodData> gods;
    public ChooseYourGodRequest(String username, List<GodData> gods){
        super(username, Content.CHOOSE_GOD);
        this.gods = gods;
    }
}
