package it.polimi.ingsw.network.message;

public class QueueResponse extends MessageResponse {

    public QueueResponse(String outcome, String username){
        super(outcome, username, Content.QUEUE);
    }
}
