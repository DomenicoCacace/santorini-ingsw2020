package it.polimi.ingsw.network.message.response;

public class ChooseNumberOfPlayerResponse extends MessageResponse {

    public int numberOfPlayers;

    public ChooseNumberOfPlayerResponse(String outcome, String username, int payload) {
        super(outcome, username, Content.CHOOSE_PLAYER_NUMBER);
        this.numberOfPlayers = payload;
    }
}
