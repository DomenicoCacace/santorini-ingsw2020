package it.polimi.ingsw.network.message;

public class EndTurnResponse extends MessageResponse {

    public EndTurnResponse(String outcome, String username) {
        super(outcome, username, Content.END_TURN);
    }
}
