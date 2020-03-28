package it.polimi.ingsw.model.utilities;

public interface Memento<T> {

    T saveState();

    void restoreState(T savedState);

}
