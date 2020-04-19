package it.polimi.ingsw.model.dataClass;

import it.polimi.ingsw.model.Cell;

import java.util.List;

public class GameData {
    private final List<Cell> board;
    private final List<PlayerData> players;
    private final TurnData currentTurn;

    public GameData(List<Cell> board, List<PlayerData> players, TurnData currentTurn) {
        this.board = board;
        this.players = players;
        this.currentTurn = currentTurn;
    }

    public List<Cell> getBoard() {
        return board;
    }

    public List<PlayerData> getPlayers() {
        return players;
    }

    public TurnData getCurrentTurn() {
        return currentTurn;
    }
}
