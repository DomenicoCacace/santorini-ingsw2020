package it.polimi.ingsw.model;

import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.dataClass.GameData;

import java.util.List;

public interface GameInterface {

    List<Cell> buildBoardData();

    boolean hasFirstPlayerLost();

    GameData buildGameData();

    void addMoveActionListener(MoveActionListener moveActionListener);

    void addEndTurnListener(EndTurnListener endTurnListener);

    void addBuildActionListener(BuildActionListener buildActionListener);

    void addEndGameListener(EndGameListener endGameListener);

    void addPlayerLostListener(PlayerLostListener playerLostListener);

    void restoreState();
}
