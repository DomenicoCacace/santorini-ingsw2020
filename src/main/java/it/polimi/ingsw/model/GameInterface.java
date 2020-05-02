package it.polimi.ingsw.model;

import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.dataClass.GameData;

import java.util.List;

public interface GameInterface {

    List<Cell> buildBoardData();

    GameData buildGameData();

    void setMoveActionListener(MoveActionListener moveActionListener);

    void setEndTurnListener(EndTurnListener endTurnListener);

    void setBuildActionListener(BuildActionListener buildActionListener);

    void setEndGameListener(EndGameListener endGameListener);

    void setPlayerLostListener(PlayerLostListener playerLostListener);

    void restoreState();
}
