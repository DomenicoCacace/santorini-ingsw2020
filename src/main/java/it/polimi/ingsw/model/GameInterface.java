package it.polimi.ingsw.model;

import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.dataClass.GameData;

import java.util.List;

/**
 * Game interface
 * <p>
 *     Offers a series of methods to change the game state
 * </p>
 */
public interface GameInterface {

    /**
     * Clones the game's gameBoard as a list of cells
     * @return a clone of the gameBoard
     */
    List<Cell> buildBoardData();

    /**
     * Determines if the current player has lost
     * @return true if the current player lost, false otherwise
     */
    boolean hasFirstPlayerLost();

    /**
     * Creates a {@linkplain GameData} object using this object's information
     * @return this object's data class
     */
    GameData buildGameData();

    /**
     * Adds a new {@linkplain MoveActionListener} to the corresponding list
     * @param moveActionListener the listener to add
     */
    void addMoveActionListener(MoveActionListener moveActionListener);

    /**
     * Adds a new {@linkplain EndTurnListener} to the corresponding list
     * @param endTurnListener the listener to add
     */
    void addEndTurnListener(EndTurnListener endTurnListener);

    /**
     * Adds a new {@linkplain BuildActionListener} to the corresponding list
     * @param buildActionListener the listener to add
     */
    void addBuildActionListener(BuildActionListener buildActionListener);

    /**
     * Adds a new {@linkplain EndGameListener} to the corresponding list
     * @param endGameListener the listener to add
     */
    void addEndGameListener(EndGameListener endGameListener);

    /**
     * Adds a new {@linkplain PlayerLostListener} to the corresponding list
     * @param playerLostListener the listener to add
     */
    void addPlayerLostListener(PlayerLostListener playerLostListener);

    /**
     * Restores the game to a previously saved state
     */
    void restoreState();
}
