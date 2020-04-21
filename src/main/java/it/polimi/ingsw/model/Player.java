package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.ObserverPattern.ObserverInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.response.fromServerToClient.AddWorkerResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.BuildableCellsResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.WalkableCellsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "idPlayer", scope = Player.class)
@JsonPropertyOrder({"idPlayer", "name", "color", "workers"}) //Pulire
public class Player implements PlayerInterface {
    private final String name;
    private final Color color;
    private final List<Worker> workers;
    private Worker selectedWorker;
    private final God god;
    private EnumMap<Event, ArrayList<ObserverInterface>> observers;
    @JsonIgnore
    private Game game;


    public Player(@JsonProperty("name") String name, @JsonProperty("god") God god, @JsonProperty("color") Color color) {
        this.name = name;
        this.god = god;
        this.color = color;
        this.workers = new ArrayList<>();
        observers = new EnumMap<>(Event.class);

    }

    private Player(Player player, Game game) {
        this.game = game;
        this.name = player.name;
        this.color = player.color;
        this.workers = new ArrayList<>();
        for (Worker worker : player.workers) {
            this.workers.add(worker.cloneWorker());
        }
        game.setCellsReferences(this);
        this.god = player.god.cloneGod(game);
    }

    public void setGame(Game game) {
        this.game = game;
        god.getStrategy().setGame(game); //We can either do this here or in the Server class after it called the game constructor.
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    @Override
    public void addWorker(Cell cell) throws AddingFailedException {
        if (cell.getOccupiedBy() == null && workers.size() < god.getWorkersNumber()) {
            Worker worker = new Worker(cell, color);
            workers.add(worker);
            cell.setOccupiedBy(worker);
            Message message = new AddWorkerResponse("OK", "broadcast", game.buildBoardData());
            notifyObservers(Event.ADD_WORKER, message);
        } else {
            throw new AddingFailedException();
        }
    }

    @Override
    public void useAction(Action action) throws IOException, IllegalActionException {
        action.getValidation(game);
    }

    @Override
    public void askPassTurn() throws IOException, IllegalEndingTurnException {
        game.endTurn();
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

    @Override
    public void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException {
        if (workers.contains(selectedWorker))
            this.selectedWorker = selectedWorker;
        else
            throw new NotYourWorkerException();
    }

    @Override
    public void obtainWalkableCells() throws WrongSelectionException {
        if (selectedWorker != null) {
            List<Cell> walkableCells = new ArrayList<>();
            for (Cell cell : game.getWalkableCells(selectedWorker)) {
                walkableCells.add(cell.cloneCell());
            }
            Message message = new WalkableCellsResponse("OK", name, walkableCells);
            notifyObservers(Event.WALKABLE_CELLS, message);
        } else
            throw new WrongSelectionException();
    }

    @Override
    public void obtainBuildableCells() throws WrongSelectionException {
        if (selectedWorker != null) {
            List<Cell> buildableCells = new ArrayList<>();
            for (Cell cell : game.getBuildableCells(selectedWorker)) {
                buildableCells.add(cell.cloneCell());
            }
            Message message = new BuildableCellsResponse("OK", name, buildableCells);
            notifyObservers(Event.BUILDABLE_CELLS, message);
        } else
            throw new WrongSelectionException();
    }

    public Player clonePlayer(Game game) {
        return new Player(this, game);
    }

    public PlayerData buildDataClass() {
        List<Worker> workersData = new ArrayList<>();
        this.workers.forEach(worker -> workersData.add(worker.cloneWorker()));
        return new PlayerData(this.name, this.color, workersData, god.buildDataClass());
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

    @Override
    public void addObserver(ObserverInterface observer, Event event) {
        ArrayList<ObserverInterface> observerList = observers.computeIfAbsent(event, k -> new ArrayList<>());
        if (!observerList.contains(observer)) {
            observers.get(event).add(observer);
        }
    }

    @Override
    public void removeObserver(ObserverInterface observer, Event event) {
        try {
            observers.get(event).remove(observer);
        } catch (Exception e) {
            // do nothing
        }
    }

    @Override
    public void notifyObservers(Event event, Message message) {
        if (observers.containsKey(event)) {
            for (ObserverInterface observerInterface : observers.get(event))
                observerInterface.update(message);
        }
    }
}
