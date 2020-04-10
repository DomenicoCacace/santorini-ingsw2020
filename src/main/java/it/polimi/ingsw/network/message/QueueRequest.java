package it.polimi.ingsw.network.message;

public class QueueRequest extends MessageRequest {

    public QueueRequest(String username) {
        super(username, Content.QUEUE);
    }
}
