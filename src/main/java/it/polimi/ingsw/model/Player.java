package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.dataClass.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "idPlayer", scope = Player.class)
@JsonPropertyOrder({"idPlayer", "name", "color", "workers", "selectedWorker"})
public class Player implements PlayerInterface {

    private final String name;
    private final Color color;
    private final List<Worker> workers;
    private final God god;
    private Worker selectedWorker;

    private AddWorkerListener addWorkerListener;
    private BuildableCellsListener buildableCellsListener;
    private WalkableCellsListener walkableCellsListener;
    private SelectWorkerListener selectWorkerListener;
    private BuildingBlocksListener buildingBlocksListener;
    @JsonIgnore
    private Game game;

    //Used by jackson to deserialize
    @JsonCreator
    private Player(@JsonProperty("name") String name, @JsonProperty("god") God god, @JsonProperty("color") Color color,
                   @JsonProperty("workers") List<Worker> workers, @JsonProperty("selectedWorker") Worker selectedWorker) {
        this.name = name;
        this.god = god;
        this.color = color;
        this.workers = workers;
        this.selectedWorker = selectedWorker;
    }

    public Player(String name, God god, Color color) {
        this.name = name;
        this.god = god;
        this.color = color;
        this.workers = new ArrayList<>();

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
            Worker worker = new Worker(game.getGameBoard().getCell(cell), color);
            workers.add(worker);
            //game.getGameBoard().getCell(cell).setOccupiedBy(worker);
            if (addWorkerListener != null)
                addWorkerListener.onWorkerAdd(game.buildBoardData());
        } else {
            throw new AddingFailedException();
        }
    }

    @Override
    public boolean allWorkersArePlaced() {
        return god.getWorkersNumber() == workers.size();
    }

    @Override
    public void useAction(Action action) throws IllegalActionException {
        action.getValidation(game);
        if (game.getCurrentTurn().getCurrentPlayer().equals(this)) {
            List<PossibleActions> possibleActions = god.getStrategy().getPossibleActions(this.selectedWorker);
            //possibleActions.remove(PossibleActions.SELECT_OTHER_WORKER);
            if (selectWorkerListener != null)
                selectWorkerListener.onSelectedWorker(name, possibleActions, selectedWorker);
        }

    }

    @Override
    public void askPassTurn() throws IllegalEndingTurnException {
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

    @JsonGetter
    private Worker getSelectedWorker() {
        return selectedWorker;
    }

    @Override
    public void obtainBuildingBlocks(Cell selectedCell) throws IllegalActionException {
        List<Block> buildingBlocks = god.getStrategy().getBlocks(selectedCell);
        if (buildingBlocks.size() == 1) {
            BuildAction buildAction = new BuildAction(selectedWorker, selectedCell, buildingBlocks.get(0));
            useAction(buildAction);
        } else if (buildingBlocksListener != null)
            buildingBlocksListener.onBlocksObtained(name, buildingBlocks);
    }

    @Override
    public void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException {
        if (workers.contains(selectedWorker)) {
            for (Worker worker : this.workers) {
                if (worker.getPosition().equals(selectedWorker.getPosition()))
                    this.selectedWorker = worker;
            }
            if (selectWorkerListener != null)
                selectWorkerListener.onSelectedWorker(name, god.getStrategy().getPossibleActions(this.selectedWorker), this.selectedWorker);
        } else
            throw new NotYourWorkerException();
    }

    /*
    public void resetSelectedWorker(){
        selectedWorker = null;
    }

    @Override
    public boolean isSelectedWorker(){
        return selectedWorker != null;
    }

     */

    @Override
    public void obtainWalkableCells() throws WrongSelectionException {
        if (selectedWorker != null) {
            List<Cell> walkableCells = new ArrayList<>();
            for (Cell cell : game.getWalkableCells(selectedWorker)) {
                walkableCells.add(cell.cloneCell());
            }
            if (walkableCellsListener != null)
                walkableCellsListener.onWalkableCells(name, walkableCells);
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
            if (buildableCellsListener != null)
                buildableCellsListener.onBuildableCell(name, buildableCells);
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

    public void setAddWorkerListener(AddWorkerListener addWorkerListener) {
        this.addWorkerListener = addWorkerListener;
    }

    public void setBuildableCellsListener(BuildableCellsListener buildableCellsListener) {
        this.buildableCellsListener = buildableCellsListener;
    }

    public void setWalkableCellsListener(WalkableCellsListener walkableCellsListener) {
        this.walkableCellsListener = walkableCellsListener;
    }

    public void setSelectWorkerListener(SelectWorkerListener selectWorkerListener) {
        this.selectWorkerListener = selectWorkerListener;
    }

    public void setBuildingBlocksListener(BuildingBlocksListener buildingBlocksListener) {
        this.buildingBlocksListener = buildingBlocksListener;
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
