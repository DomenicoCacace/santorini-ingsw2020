package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

/**
 * Listens for a player loss
 */
public interface PlayerLostListener {

    /**
     * Notifies that a player lost
     *
     * @param username  the loser's username
     * @param gameBoard the changed gameBoard without the loser's workers as a list of cells
     */
    void onPlayerLoss(String username, List<Cell> gameBoard);
}
