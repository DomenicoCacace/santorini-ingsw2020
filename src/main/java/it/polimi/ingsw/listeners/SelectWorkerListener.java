package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;

import java.util.List;

public interface SelectWorkerListener {

    void onSelectedWorker(String username, List<PossibleActions> possibleActions, Worker selectedWorker);

}
