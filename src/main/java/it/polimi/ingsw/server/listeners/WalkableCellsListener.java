package it.polimi.ingsw.server.listeners;

import it.polimi.ingsw.shared.dataClasses.Cell;

import java.util.List;

/**
 * Listens for a successful walkable cells request
 */
public interface WalkableCellsListener {

    /**
     * Notifies that there are walkable cells
     *
     * @param name          the user to notify
     * @param walkableCells the walkable cells
     */
    void onWalkableCells(String name, List<Cell> walkableCells);
}
