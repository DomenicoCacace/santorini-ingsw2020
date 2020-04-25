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

    void setAddWorkerListener(AddWorkerListener addWorkerListener);

    void setBuildableCellsListener(BuildableCellsListener buildableCellsListener);

    void setWalkableCellsListener(WalkableCellsListener walkableCellsListener);

    void setSelectWorkerListener(SelectWorkerListener selectWorkerListener);

    void setBuildingBlocksListener(BuildingBlocksListener buildingBlocksListener);

    boolean allWorkersArePlaced();
}
