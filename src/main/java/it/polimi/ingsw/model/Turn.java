package it.polimi.ingsw.model;

import it.polimi.ingsw.model.godCardEffects.RuleSetStrategy;
import it.polimi.ingsw.model.utilities.Memento;

public class Turn {
    private final int turnNumber;
    private final Player currentPlayer;
    private final RuleSetStrategy ruleSetStrategy;


    public Turn(int turnNumber, Player currentPlayer) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }


    public int getTurnNumber() {
        return turnNumber;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public RuleSetStrategy getRuleSetStrategy() {
        return ruleSetStrategy;
    }

}
