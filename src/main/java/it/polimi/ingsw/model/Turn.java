package it.polimi.ingsw.model;

import it.polimi.ingsw.model.godCardEffects.RuleSetStrategy;
import it.polimi.ingsw.model.utilities.Memento;

public class Turn implements Memento<Turn> {
    private int turnNumber;
    private Player currentPlayer;
    private RuleSetStrategy ruleSetStrategy;


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

    @Override
    public Turn saveState() {
        return new Turn(this.turnNumber, this.currentPlayer);
    }

    @Override
    public void restoreState(Turn savedState) {
        this.turnNumber = savedState.getTurnNumber();
        this.currentPlayer = savedState.getCurrentPlayer();
        this.ruleSetStrategy = savedState.getRuleSetStrategy();
    }
}
