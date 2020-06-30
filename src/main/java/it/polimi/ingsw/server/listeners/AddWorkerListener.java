package it.polimi.ingsw.server.listeners;

import it.polimi.ingsw.shared.dataClasses.Cell;

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
