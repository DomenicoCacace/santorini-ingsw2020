package it.polimi.ingsw.model.dataClass;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Worker;

import java.util.List;

public class PlayerData {
    private final String name;
    private final Color color;
    private final List<Worker> workers;
    private final GodData god;

    @JsonCreator
    public PlayerData(@JsonProperty("name") String name, @JsonProperty("color") Color color,
                      @JsonProperty("workers") List<Worker> workers, @JsonProperty("god") GodData god) {
        this.name = name;
        this.color = color;
        this.workers = workers;
        this.god = god;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public GodData getGod() {
        return god;
    }
}
