package it.polimi.ingsw.listeners;

/**
 * Listens tor the end of the game
 */
public interface EndGameListener {

    /**
     * Notifies that the game ended and a winner has been declared
     *
     * @param name the winner's username
     */
    void onEndGame(String name);
}
