package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.model.action.Action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="idPlayer", scope = Player.class)
@JsonPropertyOrder({"idPlayer","name", "color", "workers"}) //Pulire
public class Player {
    private final String name;
    private final Color color;
    private final List<Worker> workers;
    private final God god;
    private Worker selectedWorker;
    private Cell selectedCell;
    private Block selectedBlock;
    private Action action;
    private boolean disconnected;
    @JsonIgnore
    private Game game;



    public Player(@JsonProperty("name")String name,@JsonProperty("god") God god,@JsonProperty("color") Color color) {
        this.name = name;
        this.god = god;
        this.color = color;
        this.workers = new ArrayList<>();
    }

    public void setGame(Game game) {
        this.game = game;
        god.getStrategy().setGame(game); //We can either do this here or in the Server class after it called the game constructor.
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void addWorker(Cell cell) {
        if (cell.getOccupiedBy() == null) {
            Worker worker = new Worker(cell);
            workers.add(worker);
            cell.setOccupiedBy(worker);
        } else {
            //TODO: manage already occupied cell
        }
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void useAction() throws IOException, LostException {
        action.getValidation(game);
    }

    public God getGod() {
        return god;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setSelectedWorker(Worker selectedWorker) throws LostException, IOException {
        this.selectedWorker = selectedWorker;
        selectedWorker.setWalkableCells(game.getWalkableCells(selectedWorker));
    }

    public void setSelectedCell(Cell selectedCell) {
        this.selectedCell = selectedCell;
    }

    public void setSelectedBlock(Block selectedBlock) {
        this.selectedBlock = selectedBlock;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(Boolean disconnected) {
        this.disconnected = disconnected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
