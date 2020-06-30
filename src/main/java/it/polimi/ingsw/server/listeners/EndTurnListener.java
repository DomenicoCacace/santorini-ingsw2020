package it.polimi.ingsw.server.listeners;


import it.polimi.ingsw.shared.dataClasses.Cell;

import java.util.List;

/**
 * Listens for the end of a turn to happen
 */
public interface EndTurnListener {

    /**
     * Notifies that the current turn has ended
     *
     * @param name the next player's username
     */
    void onTurnEnd(String name, List<Cell> workersCells);
}
