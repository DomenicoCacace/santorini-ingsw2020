package it.polimi.ingsw.model.dataClass;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Worker;

import java.util.List;

/**
 * A data class for {@linkplain it.polimi.ingsw.model.Player}
 * <p>
 * It can be useful to send game information over the network, without any reference to the actual game object.
 */
public class PlayerData {
    private final String name;
    private final Color color;
    private final List<Worker> workers;
    private final GodData god;
    private final Worker selectedWorker;

    /**
     * Default constructor
     *
     * @param name           the player's username
     * @param color          the player's workers' color
     * @param workers        the player0s workers
     * @param god            the player's god
     * @param selectedWorker the last worker selected to perform an action
     */
    @JsonCreator
    public PlayerData(@JsonProperty("name") String name, @JsonProperty("color") Color color,
                      @JsonProperty("workers") List<Worker> workers, @JsonProperty("god") GodData god,
                      @JsonProperty("selectedWorker") Worker selectedWorker) {
        this.name = name;
        this.color = color;
        this.workers = workers;
        this.god = god;
        this.selectedWorker = selectedWorker;
    }

    /**
     * <i>selectedWorker</i> getter
     *
     * @return the last worker selected to perform an action
     */
    public Worker getSelectedWorker() {
        return selectedWorker;
    }

    /**
     * <i>name</i> getter
     *
     * @return the player's username
     */
    public String getName() {
        return name;
    }

    /**
     * <i>color</i> getter
     *
     * @return the player's workers' color
     */
    public Color getColor() {
        return color;
    }

    /**
     * <i>workers</i> getter
     *
     * @return the player's workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * <i>god</i> gettter
     *
     * @return the player's god
     */
    public GodData getGod() {
        return god;
    }
}
