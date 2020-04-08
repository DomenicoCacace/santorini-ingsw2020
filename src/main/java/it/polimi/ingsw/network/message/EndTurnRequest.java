package it.polimi.ingsw.network.message;

public class EndTurnRequest extends Message {
    public EndTurnRequest(String username) {
        super(username, Content.END_TURN);
    }
}
