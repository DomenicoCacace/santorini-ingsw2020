package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

/**
 * Listens for a successful buildable cells request
 */
public interface BuildableCellsListener {

    /**
     * Notifies that there are buildable cells
     *
     * @param name           the user to notify
     * @param buildableCells the buildable cells
     */
    void onBuildableCell(String name, List<Cell> buildableCells);
}
