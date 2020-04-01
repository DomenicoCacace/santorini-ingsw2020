package it.polimi.ingsw.model;

import it.polimi.ingsw.model.godCardEffects.RuleSetStrategy;

public class God {
    private RuleSetStrategy strategy;
    private final String name;

    public God(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public RuleSetStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }
}
