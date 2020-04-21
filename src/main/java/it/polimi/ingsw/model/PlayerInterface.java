package it.polimi.ingsw.model;

import it.polimi.ingsw.ObserverPattern.ObservableInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.action.Action;

import java.io.IOException;

public interface PlayerInterface extends ObservableInterface {

    void addWorker(Cell cell) throws AddingFailedException;

    void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException;

    void obtainWalkableCells() throws WrongSelectionException;

    void obtainBuildableCells() throws WrongSelectionException;

    void useAction(Action action) throws IOException, IllegalActionException;

    void askPassTurn() throws IOException, IllegalEndingTurnException;

}
