package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.action.Action;

public interface PlayerInterface {

    void addWorker(Cell cell) throws AddingFailedException;

    void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException;

    void obtainWalkableCells() throws WrongSelectionException;

    void obtainBuildableCells() throws WrongSelectionException;

    void obtainBuildingBlocks(Cell selectedCell) throws IllegalActionException;

    void useAction(Action action) throws IllegalActionException;

    void askPassTurn() throws IllegalEndingTurnException;

    void addWorkerListener(AddWorkerListener addWorkerListener);

    void addBuildableCellsListener(BuildableCellsListener buildableCellsListener);

    void addWalkableCellsListener(WalkableCellsListener walkableCellsListener);

    void addSelectWorkerListener(SelectWorkerListener selectWorkerListener);

    void addBuildingBlocksListener(BuildingBlocksListener buildingBlocksListener);

    boolean allWorkersArePlaced();

   // boolean isSelectedWorker();
}
