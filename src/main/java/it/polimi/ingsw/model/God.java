package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "GodId", scope = God.class)
public class God {
    private final String name;
    private final int workersNumber;
    private RuleSetStrategy strategy;
    private String descriptionStrategy;

    private God(God god, Game game) {
        this.name = god.name;
        this.strategy = god.strategy.cloneStrategy(game);
        this.workersNumber = god.workersNumber;
    }

    public God(@JsonProperty("name") String name, @JsonProperty("workersNumber") int workersNumber, @JsonProperty("descriptionStrategy") String descriptionStrategy) {
        this.name = name;
        this.workersNumber = workersNumber;
        this.descriptionStrategy = descriptionStrategy;
    }


    public God cloneGod(Game game) {
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


    /*  As of now we build GodData objects using json files, we cannot build a GodData obj here because
        we don't have "description" parameter  inside the God class, we have 2 options:
            -Using a file for every god named "godName.json" where we keep the serialized gameDataObject
             and we deserialized it everytime we need to build a godDataClass
            -Adding the String description somewhere inside the God class or the strategy class (so we can
             put description inside the god Apollo or inside Swap strategy)
    */
    public GodData buildDataClass() {
        return new GodData(name, workersNumber, descriptionStrategy);
    }

}
