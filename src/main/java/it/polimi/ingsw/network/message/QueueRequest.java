package it.polimi.ingsw.network.message;

public class QueueRequest extends Message {

    public QueueRequest(String username) {
        super(username, Content.QUEUE);
    }
}
