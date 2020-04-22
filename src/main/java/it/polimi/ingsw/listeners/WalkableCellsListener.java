package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

public interface WalkableCellsListener {
    void onWalkableCells(String name, List<Cell> walkableCells);
}
