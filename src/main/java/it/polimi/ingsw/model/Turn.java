package it.polimi.ingsw.model;

import java.util.List;

public class Turn {
    private int turnNumber;
    private Player currentPlayer;
    private RuleSetStrategy ruleSetStrategy;
    private List<Player> playersOrder;

    public Turn(int turnNumber, Player currentPlayer, RuleSetStrategy ruleSetStrategy) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
        this.ruleSetStrategy = ruleSetStrategy;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public RuleSetStrategy getRuleSetStrategy() {
        return ruleSetStrategy;
    }

    public void setRuleSetStrategy(RuleSetStrategy ruleSetStrategy) {
        this.ruleSetStrategy = ruleSetStrategy;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    public void setPlayersOrder(List<Player> playersOrder) {
        this.playersOrder = playersOrder;
    }
}
