package it.polimi.ingsw;


import java.util.ArrayList;

public class Game {
    private Turn currentTurn, nextTurn;
    private GameBoard gameBoard;
    private Player winner;
    private ArrayList<God> choosenGods[];
    private RuleSetstrategy currentRules;
    private ArrayList<Player> players[];


    public Game(GameBoard gameBoard, ArrayList<Player>[] players, ArrayList<God>[] choosenGods) {
        this.currentTurn = new Turn();
        this.gameBoard = gameBoard;
        this.players = players;
        this.choosenGods = choosenGods;
    }

    public Turn createNextTurn(){

    }
}
