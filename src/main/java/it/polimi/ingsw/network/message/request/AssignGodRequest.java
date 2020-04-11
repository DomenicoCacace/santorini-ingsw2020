package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.God;

public class AssignGodRequest extends MessageRequest {

    public AssignGodRequest(String username, God god){
        super(username, Content.ASSIGN_GOD);
    }

}
