package it.polimi.ingsw.model;

import it.polimi.ingsw.ObserverPattern.ObservableInterface;
import it.polimi.ingsw.model.dataClass.GameData;

import java.util.List;

public interface GameInterface extends ObservableInterface {

    List<Cell> buildBoardData();

    GameData buildGameData();
}
