package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.ObserverPattern.ObservableInterface;
import it.polimi.ingsw.ObserverPattern.ObserverInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.network.message.response.MessageResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.AddWorkerResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.BuildableCellsResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.WalkableCellsResponse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="idPlayer", scope = Player.class)
@JsonPropertyOrder({"idPlayer","name", "color", "workers"}) //Pulire
public class Player implements ObservableInterface {
    private final String name;
    private final Color color;
    private final List<Worker> workers;
    private Worker selectedWorker;
    private final God god;
    private Action action;
    private EnumMap<Event, ArrayList<ObserverInterface>> observers;
    @JsonIgnore
    private Game game;



    public Player(@JsonProperty("name")String name,@JsonProperty("god") God god,@JsonProperty("color") Color color) {
        this.name = name;
        this.god = god;
        this.color = color;
        this.workers = new ArrayList<>();
    }

    public Player(Player player, Game game){
        this.game = game;
        this.name = player.name;
        this.color = player.color;
        this.workers = new ArrayList<>();
        for(Worker worker: player.workers){
            this.workers.add(new Worker(worker.getPosition(), color));
        }
        game.setCellsReferences(this);
        this.god = new God(player.god, game);
    }

    public void setGame(Game game) {
        this.game = game;
        god.getStrategy().setGame(game); //We can either do this here or in the Server class after it called the game constructor.
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void addWorker(Cell cell) throws AddingFailedException {
        if (cell.getOccupiedBy() == null && workers.size()<god.getWorkersNumber()) {
            Worker worker = new Worker(cell, color);
            workers.add(worker);
            cell.setOccupiedBy(worker);
            MessageResponse messageResponse = new AddWorkerResponse("OK", name, game.cloneGameBoard());
            notifyObservers(Event.ADD_WORKER, messageResponse);
        } else {
            throw new AddingFailedException();
        }
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void useAction() throws IOException, IllegalActionException {
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

    public void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException {
        if(workers.contains(selectedWorker))
            this.selectedWorker = selectedWorker;
        else
            throw new NotYourWorkerException();
    }

    public void obtainWalkableCells() throws WrongSelectionException {
        if(selectedWorker != null){
            List<Cell> walkableCells = new ArrayList<>();
            for (Cell cell : game.getWalkableCells(selectedWorker)) {
                walkableCells.add(new Cell(cell));
            }
            MessageResponse messageResponse = new WalkableCellsResponse("OK", name, walkableCells);
            notifyObservers(Event.WALKABLE_CELLS, messageResponse);
        }
        else
            throw new WrongSelectionException();
    }

    public void obtainBuildableCells() throws WrongSelectionException {
        if(selectedWorker != null){
            List<Cell> buildableCells = new ArrayList<>();
            for (Cell cell : game.getBuildableCells(selectedWorker)) {
                buildableCells.add(new Cell(cell));
            }
            MessageResponse messageResponse = new BuildableCellsResponse("OK", name, buildableCells);
            notifyObservers(Event.BUILDABLE_CELLS, messageResponse);
        }
        else
            throw new WrongSelectionException();
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
        if(!observerList.contains(observer)){
            observers.get(event).add(observer);
        }
    }

    @Override
    public void removeObserver(ObserverInterface observer, Event event) {
        try {
            observers.get(event).remove(observer);
        } catch (Exception e){
            // do nothing
        }
    }

    @Override
    public void notifyObservers(Event event, MessageResponse messageResponse) {
        for(ObserverInterface observerInterface : observers.get(event))
            observerInterface.update(messageResponse);
    }
}
