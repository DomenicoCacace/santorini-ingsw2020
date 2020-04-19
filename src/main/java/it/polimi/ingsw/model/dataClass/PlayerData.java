package it.polimi.ingsw.model.dataClass;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Worker;

import java.util.List;

public class PlayerData {
    private final String name;
    private final Color color;
    private final List<Worker> workers;
    private final GodData god;

    public PlayerData(String name, Color color, List<Worker> workers, GodData god) {
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
