package it.polimi.ingsw.model.dataClass;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "GodId", scope = it.polimi.ingsw.model.dataClass.GodData.class)
public class GodData {

    private final String name;
    private final int workersNumber;
    private final String descriptionStrategy;

    @JsonCreator
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GodData)) return false;
        GodData godData = (GodData) o;
        return workersNumber == godData.workersNumber &&
                name.equals(godData.name) &&
                Objects.equals(descriptionStrategy, godData.descriptionStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, workersNumber, descriptionStrategy);
    }
}