package it.polimi.ingsw.model.dataClass;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="GodId", scope = it.polimi.ingsw.model.dataClass.GodData.class)
public class GodData {

    private final String name;
    private final int workersNumber;
    private String descriptionStrategy;

    public GodData(@JsonProperty("name") String name, @JsonProperty("workersNumber") int workersNumber, @JsonProperty("descriptionStrategy") String descriptionStrategy) {
        this.name = name;
        this.workersNumber = workersNumber;
        this.descriptionStrategy = descriptionStrategy;
    }

    
    public String getName() {
        return name;
    }

    public int getWorkersNumber() {
        return workersNumber;
    }

    public String getDescriptionStrategy() {
        return descriptionStrategy;
    }
}