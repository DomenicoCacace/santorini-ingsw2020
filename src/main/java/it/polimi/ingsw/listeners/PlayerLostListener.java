package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cell;
import java.util.List;

public interface PlayerLostListener {

    void onPlayerLoss(String username, List<Cell> gameboard);
}
