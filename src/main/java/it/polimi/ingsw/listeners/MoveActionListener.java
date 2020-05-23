package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

/**
 * Listens for a legal move action to happen
 */
public interface MoveActionListener {

    /**
     * Notifies that a successful move action happened
     *
     * @param gameBoard the changed gameBoard as a list of cells
     */
    void onMoveAction(List<Cell> gameBoard);

}
