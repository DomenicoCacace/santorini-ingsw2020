package it.polimi.ingsw.server.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.listeners.*;
import it.polimi.ingsw.server.model.action.Action;
import it.polimi.ingsw.server.model.action.BuildAction;
import it.polimi.ingsw.shared.dataClasses.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Representation of a Player during a game
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "idPlayer", scope = Player.class)
@JsonPropertyOrder({"idPlayer", "name", "color", "workers", "selectedWorker"})
public class Player implements PlayerInterface {

    private final String name;
    private final Color Color;
    private final List<Worker> workers;
    private final God god;
    private final List<AddWorkerListener> addWorkerListener = new ArrayList<>();
    private final List<BuildableCellsListener> buildableCellsListener = new ArrayList<>();
    private final List<WalkableCellsListener> walkableCellsListener = new ArrayList<>();
    private final List<SelectWorkerListener> selectWorkerListener = new ArrayList<>();
    private final List<BuildingBlocksListener> buildingBlocksListener = new ArrayList<>();
    private Worker selectedWorker;
    @JsonIgnore
    private Game game;

    /**
     * Copy constructor
     *
     * @param name           the player's name
     * @param god            the player's god
     * @param Color          the player's workers color
     * @param workers        the player's workers
     * @param selectedWorker the last worker selected (might be null)
     */
    //Used by jackson to deserialize, might marked as not used by IntelliJ; do NOT delete
    @JsonCreator
    private Player(@JsonProperty("name") String name, @JsonProperty("god") God god, @JsonProperty("color") Color Color,
                   @JsonProperty("workers") List<Worker> workers, @JsonProperty("selectedWorker") Worker selectedWorker) {
        this.name = name;
        this.god = god;
        this.Color = Color;
        this.workers = workers;
        this.selectedWorker = selectedWorker;
    }

    /**
     * Creates a new Player, assigning its {@link #name}, the {@link #god} it chose and the {@link #Color}
     * assigned to its workers.
     * <p>
     * Since some Gods allow the player to have more than two workers, the constructor initializes the
     * {@link #workers} as an empty list; the workers will then be created and assigned to the player with the
     * {@link #addWorker(Cell)} method.
     * <br>
     * Regarding the game attribute, it is not assigned in the constructor, since the {@link Game}
     * is created after the creation of the players.
     *
     * @param name  the player's username
     * @param god   the player's God card, chosen before the game is created
     * @param Color the player's workers color, automatically determined before the game is created
     */
    public Player(String name, God god, Color Color) {
        this.name = name;
        this.god = god;
        this.Color = Color;
        this.workers = new ArrayList<>();

    }

    private Player(Player player, Game game) {
        this.game = game;
        this.name = player.name;
        this.Color = player.Color;
        this.workers = new ArrayList<>();
        for (Worker worker : player.workers) {
            this.workers.add(worker.cloneWorker());
        }
        game.setCellsReferences(this);
        this.god = player.god.cloneGod(game);
    }

    /**
     * Sets what {@link #game} the Player is playing
     *
     * @param game {@link #game}
     * @see Game#Game(GameBoard, List)
     */
    public void setGame(Game game) {
        this.game = game;
        god.getStrategy().setGame(game); //We can either do this here or in the Lobby class after it called the game constructor.
    }

    /**
     * Gets all Player's workers
     *
     * @return a list containing all the player's workers {@link #workers}
     */
    public List<Worker> getWorkers() {
        return workers;
    }


    /**
     * Adds a worker to the board
     *
     * @param cell the cell to place the worker to
     * @throws AddingFailedException if the target cell is already occupied or all the workers are already placed
     */
    @Override
    public void addWorker(Cell cell) throws AddingFailedException {
        if (cell.getOccupiedBy() == null && workers.size() < god.getWorkersNumber()) {
            Worker worker = new Worker(game.getGameBoard().getCell(cell), Color);
            workers.add(worker);
            addWorkerListener.forEach(addWorkerListener1 -> addWorkerListener1.onWorkerAdd(game.buildBoardData()));
        } else {
            throw new AddingFailedException();
        }
    }

    /**
     * Checks if the player has placed all of its workers
     *
     * @return true if all the player's workers have been placed, false otherwise
     */
    @Override
    public boolean allWorkersArePlaced() {
        return god.getWorkersNumber() == workers.size();
    }

    /**
     * Applies the given action
     *
     * @param action the action to be applied
     * @throws IllegalActionException if the action cannot be performed
     */
    @Override
    public void useAction(Action action) throws IllegalActionException {
        action.getValidation(game);
        if (game.getCurrentTurn().getCurrentPlayer().equals(this)) {
            List<PossibleActions> possibleActions = god.getStrategy().getPossibleActions(this.selectedWorker);
            if (game.getWinner() == null)
                selectWorkerListener.forEach(selectWorkerListener1 -> selectWorkerListener1.onSelectedWorker(name, possibleActions, selectedWorker));
        }
    }

    /**
     * Ends the player's turn
     *
     * @throws IllegalEndingTurnException if the turn cannot be ended
     */
    @Override
    public void askPassTurn() throws IllegalEndingTurnException {
        game.endTurn();
    }

    /**
     * <i>god</i> getter
     *
     * @return the player's god
     */
    public God getGod() {
        return god;
    }

    /**
     * <i>color</i> getter
     *
     * @return the player's workers color
     */
    @SuppressWarnings("unused")
    public Color getColor() {
        return Color;
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
     * <i>selectedWorker</i> getter
     *
     * @return the worker selected to perform the next action
     */
    @JsonGetter
    @SuppressWarnings("unused")
    public Worker getSelectedWorker() {
        return selectedWorker;
    }

    /**
     * Sets the worker to perform the next action
     *
     * @param selectedWorker the worker to select
     * @throws NotYourWorkerException if the worker is not owned by the player
     */
    @Override
    public void setSelectedWorker(Worker selectedWorker) throws NotYourWorkerException {
        if (workers.contains(selectedWorker)) {
            for (Worker worker : this.workers) {
                if (worker.getPosition().equals(selectedWorker.getPosition()))
                    this.selectedWorker = worker;
            }
            selectWorkerListener.forEach(selectWorkerListener1 ->
                    selectWorkerListener1.onSelectedWorker(name, god.getStrategy().getPossibleActions(this.selectedWorker), this.selectedWorker));
        } else
            throw new NotYourWorkerException();
    }

    /**
     * Resets the worker to perform the next action to null
     */
    public void resetSelectedWorker() {
        this.selectedWorker = null;
    }


    /**
     * Provides a list of blocks which the selected worker can build on the given cell
     *
     * @param selectedCell the cell to perform the build action on
     * @throws IllegalActionException if the build action cannot be performed
     */
    @Override
    public void obtainBuildingBlocks(Cell selectedCell) throws IllegalActionException {
        List<Block> buildingBlocks = god.getStrategy().getBlocks(selectedCell);
        if (buildingBlocks.size() == 1) {
            BuildAction buildAction = new BuildAction(selectedWorker, selectedCell, buildingBlocks.get(0));
            useAction(buildAction);
        } else
            buildingBlocksListener.forEach(buildingBlocksListener1 -> buildingBlocksListener1.onBlocksObtained(name, buildingBlocks));
    }

    /**
     * Provides a list of cells on which the selected player can walk to
     *
     * @throws WrongSelectionException if no worker has been selected
     */
    @Override
    public void obtainWalkableCells() throws WrongSelectionException {
        if (selectedWorker != null) {
            List<Cell> walkableCells = new ArrayList<>();
            for (Cell cell : game.getWalkableCells(selectedWorker)) {
                walkableCells.add(cell.cloneCell());
            }
            walkableCellsListener.forEach(walkableCellsListener1 -> walkableCellsListener1.onWalkableCells(name, walkableCells));
        } else
            throw new WrongSelectionException();
    }

    /**
     * Provides a list of cells on which the selected player can build on
     *
     * @throws WrongSelectionException if no worker has been selected
     */
    @Override
    public void obtainBuildableCells() throws WrongSelectionException {
        if (selectedWorker != null) {
            List<Cell> buildableCells = new ArrayList<>();
            for (Cell cell : game.getBuildableCells(selectedWorker)) {
                buildableCells.add(cell.cloneCell());
            }
            buildableCellsListener.forEach(buildableCellsListener1 -> buildableCellsListener1.onBuildableCell(name, buildableCells));
        } else
            throw new WrongSelectionException();
    }

    /**
     * Creates a clone of this object
     *
     * @param game the current game
     * @return a clone of this object
     */
    public Player clonePlayer(Game game) {
        return new Player(this, game);
    }

    /**
     * Creates a {@linkplain PlayerData} object based on this player
     *
     * @return this object's data class
     */
    public PlayerData buildDataClass() {
        List<Worker> workersData = new ArrayList<>();
        this.workers.forEach(worker -> workersData.add(worker.cloneWorker()));
        if (selectedWorker != null)
            return new PlayerData(this.name, this.Color, workersData, god.buildDataClass(), selectedWorker.cloneWorker());
        return new PlayerData(this.name, this.Color, workersData, god.buildDataClass(), null);
    }

    /**
     * Adds a new listener
     *
     * @param addWorkerListener the listener to add to the list
     */
    @Override
    public void addWorkerListener(AddWorkerListener addWorkerListener) {
        this.addWorkerListener.add(addWorkerListener);
    }

    /**
     * Adds a new listener
     *
     * @param buildableCellsListener the listener to add to the list
     */
    @Override
    public void addBuildableCellsListener(BuildableCellsListener buildableCellsListener) {
        this.buildableCellsListener.add(buildableCellsListener);
    }

    /**
     * Adds a new listener
     *
     * @param walkableCellsListener the listener to add to the list
     */
    @Override
    public void addWalkableCellsListener(WalkableCellsListener walkableCellsListener) {
        this.walkableCellsListener.add(walkableCellsListener);
    }

    /**
     * Adds a new listener
     *
     * @param selectWorkerListener the listener to add to the list
     */
    @Override
    public void addSelectWorkerListener(SelectWorkerListener selectWorkerListener) {
        this.selectWorkerListener.add(selectWorkerListener);
    }

    /**
     * Adds a new listener
     *
     * @param buildingBlocksListener the listener to add to the list
     */
    @Override
    public void addBuildingBlocksListener(BuildingBlocksListener buildingBlocksListener) {
        this.buildingBlocksListener.add(buildingBlocksListener);
    }

    /**
     * Compares the argument to the receiver, and answers true if their names are equals
     *
     * @param o the object to be
     * @return true if the object is the same as the cell, false otherwise
     */
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
