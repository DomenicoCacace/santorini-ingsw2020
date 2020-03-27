package it.polimi.ingsw.model;

public class God {
    private String name;

    public God(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public RuleSetStrategy strategy;

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
