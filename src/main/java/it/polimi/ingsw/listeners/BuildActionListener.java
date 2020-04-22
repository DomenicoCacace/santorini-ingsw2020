package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

public interface BuildActionListener {
    void onBuildAction(List<Cell> cells);
}
