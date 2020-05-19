package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.action.Action;

/**
 * Player interface
 * <p>
 *     Offers a series of methods to change the player state
*/
public interface PlayerInterface {

    /**
     * Adds a worker to the board
     * @param cell the cell to place the worker to
     * @throws AddingFailedException if the worker could not be added
     */
    void addWorker(Cell cell) throws AddingFailedException;

    /**
     * Sets the worker to perform the next action
     * @param selectedWorker the worker to select
     * @throws NotYourWorkerException if the worker is not owned by the player
     */
    void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException;

    /**
     * Provides a list of cells on which the selected player can walk to
     * @throws WrongSelectionException if no worker has been selected
     */
    void obtainWalkableCells() throws WrongSelectionException;

    /**
     * Provides a list of cells on which the selected player can build on
     * @throws WrongSelectionException if no worker has been selected
     */
    void obtainBuildableCells() throws WrongSelectionException;

    /**
     * Provides a list of blocks which the selected worker can build on the given cell
     * @param selectedCell the cell to perform the build action on
     * @throws IllegalActionException if no worker has been selected
     */
    void obtainBuildingBlocks(Cell selectedCell) throws IllegalActionException;

    /**
     * Applies the given action
     * @param action the action to be applied
     * @throws IllegalActionException if the action cannot be performed
     */
    void useAction(Action action) throws IllegalActionException;

    /**
     * Ends the player's turn
     * @throws IllegalEndingTurnException if the turn cannot be ended
     */
    void askPassTurn() throws IllegalEndingTurnException;

    /**
     * Adds a new listener
     * @param addWorkerListener the listener to add to the list
     */
    void addWorkerListener(AddWorkerListener addWorkerListener);

    /**
     * Adds a new listener
     * @param buildableCellsListener the listener to add to the list
     */
    void addBuildableCellsListener(BuildableCellsListener buildableCellsListener);

    /**
     * Adds a new listener
     * @param walkableCellsListener the listener to add to the list
     */
    void addWalkableCellsListener(WalkableCellsListener walkableCellsListener);

    /**
     * Adds a new listener
     * @param selectWorkerListener the listener to add to the list
     */
    void addSelectWorkerListener(SelectWorkerListener selectWorkerListener);

    /**
     * Adds a new listener
     * @param buildingBlocksListener the listener to add to the list
     */
    void addBuildingBlocksListener(BuildingBlocksListener buildingBlocksListener);

    /**
     * Checks if the player has placed all of its workers
     * @return true if all the player's workers have been placed, false otherwise
     */
    boolean allWorkersArePlaced();
}
