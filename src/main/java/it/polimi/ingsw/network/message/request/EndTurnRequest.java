package it.polimi.ingsw.network.message.request;

public class EndTurnRequest extends MessageRequest {

    public EndTurnRequest(String username) {
        super(username, Content.END_TURN);
    }
}
