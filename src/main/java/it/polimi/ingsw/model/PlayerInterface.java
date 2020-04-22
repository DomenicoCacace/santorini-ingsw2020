package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.AddWorkerListener;
import it.polimi.ingsw.listeners.BuildableCellsListener;
import it.polimi.ingsw.listeners.WalkableCellsListener;
import it.polimi.ingsw.model.action.Action;

import java.io.IOException;

public interface PlayerInterface {

    void addWorker(Cell cell) throws AddingFailedException;

    void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException;

    void obtainWalkableCells() throws WrongSelectionException;

    void obtainBuildableCells() throws WrongSelectionException;

    void useAction(Action action) throws IllegalActionException;

    void askPassTurn() throws IllegalEndingTurnException;

    void setAddWorkerListener(AddWorkerListener addWorkerListener);

    void setBuildableCellsListener(BuildableCellsListener buildableCellsListener);

    void setWalkableCellsListener(WalkableCellsListener walkableCellsListener);
}
