package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.PossibleActions;

import java.util.List;

public interface SelectWorkerListener {

    void onSelectedWorker(String username, List<PossibleActions> possibleActions);

}
