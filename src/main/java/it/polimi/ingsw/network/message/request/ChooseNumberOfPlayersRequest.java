package it.polimi.ingsw.network.message.request;

public class ChooseNumberOfPlayersRequest extends MessageRequest {

    public ChooseNumberOfPlayersRequest(String username) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
    }
}
