package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="GodId", scope = God.class)
public class God {

    private RuleSetStrategy strategy;
    private final String name;



    public God(@JsonProperty("name") String name) {
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
