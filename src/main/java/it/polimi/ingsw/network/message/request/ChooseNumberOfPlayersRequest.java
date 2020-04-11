package it.polimi.ingsw.network.message.request;

public class ChooseNumberOfPlayers extends MessageRequest {

    public ChooseNumberOfPlayers(String username) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
    }
}
