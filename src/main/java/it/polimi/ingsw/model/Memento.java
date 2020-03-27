package it.polimi.ingsw.model;

public interface Memento<T> {

    T saveState();

    void restoreState(T savedState);




}
