package it.polimi.ingsw.ObserverPattern;


import it.polimi.ingsw.network.message.Message;

import java.io.IOException;

/**
 * ObserverInterface is used for realizing the Observer design pattern
 */
public interface ObserverInterface {
    /**
     * This method updates the Observer
     * @param updateMessage object containing information about the update
     */
    void update(Message updateMessage); //TODO: Check message type
}
