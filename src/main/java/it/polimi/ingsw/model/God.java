package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="GodId", scope = God.class)
public class God {
    private RuleSetStrategy strategy;
    private final String name;
    private final int workersNumber;


    private God(God god, Game game){
        this.name = god.name;
        this.strategy = god.strategy.cloneStrategy(game);
        this.workersNumber = god.workersNumber;
    }

    public God(@JsonProperty("name") String name, @JsonProperty("workersNumber") int workersNumber) {
        this.name = name;
        this.workersNumber = workersNumber;
    }

    public God cloneGod(Game game){
        return new God(this, game);
    }

    public String getName() {
        return name;
    }

    public int getWorkersNumber() {
        return workersNumber;
    }

    public RuleSetStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }

}
