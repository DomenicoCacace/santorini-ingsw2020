package it.polimi.ingsw.server.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.polimi.ingsw.server.model.rules.RuleSetStrategy;
import it.polimi.ingsw.shared.dataClasses.GodData;

/**
 * Representation of a god card and its effect
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class God {
    private final String name;
    private final int workersNumber;
    private final String descriptionStrategy;
    private RuleSetStrategy strategy;

    /**
     * Copy constructor
     *
     * @param god  the god to be cloned
     * @param game the game in which the god is being used
     */
    private God(God god, Game game) {
        this.name = god.name;
        this.strategy = god.strategy.cloneStrategy(game);
        this.workersNumber = god.workersNumber;
        this.descriptionStrategy = god.getDescriptionStrategy();
    }

    /**
     * Default constructor
     *
     * @param name                the god's name
     * @param workersNumber       the number of workers associated to a given god
     * @param descriptionStrategy a description of the god's power
     */
    public God(@JsonProperty("name") String name, @JsonProperty("workersNumber") int workersNumber, @JsonProperty("descriptionStrategy") String descriptionStrategy) {
        this.name = name;
        this.workersNumber = workersNumber;
        this.descriptionStrategy = descriptionStrategy;
    }

    /**
     * Creates a clone of this god
     *
     * @param game the current game
     * @return a clone of this object
     */
    public God cloneGod(Game game) {
        return new God(this, game);
    }

    /**
     * <i>name</i> getter
     *
     * @return the god's name
     */
    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    /**
     * <i>description</i> getter
     *
     * @return the god power description
     */
    public String getDescriptionStrategy() {
        return descriptionStrategy;
    }

    /**
     * <i>workersNumber</i> getter
     *
     * @return the number of workers associated with the god power
     */
    public int getWorkersNumber() {
        return workersNumber;
    }

    /**
     * <i>strategy</i> getter
     *
     * @return the strategy associated with the god effects
     */
    public RuleSetStrategy getStrategy() {
        return strategy;
    }

    /**
     * <i>strategy</i> setter
     *
     * @param strategy the strategy associated with thr god effects
     */
    public void setStrategy(RuleSetStrategy strategy) {
        this.strategy = strategy;
    }


    /**
     * Creates a {@linkplain GodData} object based on this god
     *
     * @return this object's data class
     */
    public GodData buildDataClass() {
        return new GodData(name, workersNumber, descriptionStrategy);
    }

}
