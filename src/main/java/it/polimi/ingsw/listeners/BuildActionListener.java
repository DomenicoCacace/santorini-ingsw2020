package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

/**
 * Listens for a legal build action to happen
 */
public interface BuildActionListener {

    /**
     * Notifies that a successful build action happened
     *
     * @param cells the changed gameBoard as a list of cells
     */
    void onBuildAction(List<Cell> cells);
}
