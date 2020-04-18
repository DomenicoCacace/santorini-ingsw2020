package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.Message;

public class AssignGodRequest extends Message {

    public final God god;

    public AssignGodRequest(String username, God god){
        super(username, Content.ASSIGN_GOD);
        this.god = god;
    }

}
