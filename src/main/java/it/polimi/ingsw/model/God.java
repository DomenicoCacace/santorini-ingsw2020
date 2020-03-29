package it.polimi.ingsw.model;

import it.polimi.ingsw.model.godCardEffects.RuleSetStrategy;

public class God {
    public RuleSetStrategy strategy;
    private String name;

    public God(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleSetStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }
}
