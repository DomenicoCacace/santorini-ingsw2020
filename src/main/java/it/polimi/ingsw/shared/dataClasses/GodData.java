package it.polimi.ingsw.shared.dataClasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.polimi.ingsw.server.model.God;

import java.util.Objects;

/**
 * A data class for {@linkplain God}
 * <p>
 * It can be useful to send game information over the network, without any reference to the actual game object.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "GodId", scope = GodData.class)
public class GodData {

    private final String name;
    private final int workersNumber;
    private final String descriptionStrategy;   //never used

    /**
     * Default constructor
     *
     * @param name                the god name
     * @param workersNumber       the number of workers granted from the god power
     * @param descriptionStrategy a description of the god power
     */
    @JsonCreator
    public GodData(@JsonProperty("name") String name, @JsonProperty("workersNumber") int workersNumber, @JsonProperty("descriptionStrategy") String descriptionStrategy) {
        this.name = name;
        this.workersNumber = workersNumber;
        this.descriptionStrategy = descriptionStrategy;
    }

    /**
     * <i>name</i> getter
     *
     * @return the god's name
     */
    public String getName() {
        return name;
    }

    /**
     * <i>workersNumber</i> getter
     *
     * @return the number of workers granted from the god power
     */
    public int getWorkersNumber() {
        return workersNumber;
    }

    /**
     * <i>descriptionStrategy</i> getter
     *
     * @return a description of the go power
     */
    public String getDescriptionStrategy() {
        return descriptionStrategy;
    }


    /**
     * Compares the argument to the receiver, and answers true if their names, number of workers and description are equals
     *
     * @param o the object to be
     * @return true if the object is the same as the cell, false otherwise
     */
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