package it.polimi.ingsw.model;

import it.polimi.ingsw.model.dataClass.GameData;

import java.util.List;

public interface GameInterface {

    List<Cell> buildBoardData();

    GameData buildGameData();
}
