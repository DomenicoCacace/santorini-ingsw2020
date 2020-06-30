package it.polimi.ingsw.server.listeners;

import it.polimi.ingsw.shared.dataClasses.PossibleActions;
import it.polimi.ingsw.shared.dataClasses.Worker;

import java.util.List;

/**
 * Listens for a legal worker selection
 */
public interface SelectWorkerListener {

    /**
     * Notifies that the worker choice was successful
     *
     * @param username        the player to notify's username
     * @param possibleActions a list of the actions performable with the chosen worker
     * @param selectedWorker  the selected worker
     */
    void onSelectedWorker(String username, List<PossibleActions> possibleActions, Worker selectedWorker);

}
