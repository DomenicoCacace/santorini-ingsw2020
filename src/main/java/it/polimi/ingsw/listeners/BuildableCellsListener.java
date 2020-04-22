package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;

import java.util.List;

public interface BuildableCellsListener {
    void onBuildableCell(String name, List<Cell> buildableCells);
}
