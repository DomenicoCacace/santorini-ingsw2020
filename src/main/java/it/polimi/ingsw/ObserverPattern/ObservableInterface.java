package it.polimi.ingsw.ObserverPattern;


import it.polimi.ingsw.model.Event;



public interface ObservableInterface {

    void addObserver(ObserverInterface observer, Event event);


    void removeObserver(ObserverInterface observer, Event event);


    void notifyObservers(Event event);
}
