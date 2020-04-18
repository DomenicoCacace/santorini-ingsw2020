package it.polimi.ingsw.ObserverPattern;


import it.polimi.ingsw.model.Event;
import it.polimi.ingsw.network.message.Message;

import java.io.IOException;


public interface ObservableInterface {

    void addObserver(ObserverInterface observer, Event event);


    void removeObserver(ObserverInterface observer, Event event);


    void notifyObservers(Event event, Message messageResponse) throws IOException;
}
