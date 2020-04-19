package it.polimi.ingsw.network.message.request.fromClientToServer;


import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.Message;

public class AssignGodRequest extends Message {
    public final GodData god;

    public AssignGodRequest(String username, GodData god) {
        super(username, Content.ASSIGN_GOD);
        this.god = god;
    }

}
