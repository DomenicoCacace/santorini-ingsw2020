package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.util.List;

public class ChosenGodsResponse extends MessageResponse {

    public final List<God> payload;


    public ChosenGodsResponse(String outcome, List<God> payload) {
        super(outcome, "broadcast", Content.CHOSEN_GODS);
        if(outcome.equals("OK")){
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }




}

