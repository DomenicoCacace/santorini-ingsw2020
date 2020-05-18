package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

/**
 * Listens for workers to be placed on the board
 */
public interface AddWorkerListener {

    /**
     * Notifies that a worker has been added
     *
     * @param workerCell the cell containing the newly added worker
     */
    void onWorkerAdd(List<Cell> workerCell);
}
