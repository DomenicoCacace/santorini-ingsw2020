package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.exceptions.IllegalEndingTurnException;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.dataClass.GameData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.model.dataClass.TurnData;
import it.polimi.ingsw.model.listeners.*;
import it.polimi.ingsw.model.rules.RuleSetContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Game implements GameInterface {
    private final GameBoard gameBoard;
    private final List<Player> players;
    private Turn currentTurn;
    private Turn nextTurn;
    private Player winner;
    private final RuleSetContext currentRuleSet;

    private MoveActionListener moveActionListener;
    private EndTurnListener endTurnListener;
    private BuildActionListener buildActionListener;
    private AddWorkerListener addWorkerListener;
    private BuildableCellsListener buildableCellsListener;
    private WalkableCellsListener walkableCellsListener;
    private EndGameListener endGameListener;
    private PlayerLostListener playerLostListener;

    private File file = new File("../SavedGame.json");

    public Game(GameBoard gameBoard, List<Player> players) {
        this.gameBoard = gameBoard;
        this.players = players;
        for (Player player : players)
            player.setGame(this);
        //just for testing
        currentRuleSet = new RuleSetContext();
        this.saveState();
        currentTurn = new Turn(0, players.get(players.size() - 1));
    }

    /*
        This is needed by jackson in order to correctly deserialize the .json where an old state of Game is stored, we need this to implement Persistence
     */
    private Game(@JsonProperty("gameBoard") GameBoard gameBoard, @JsonProperty("players") List<Player> players, @JsonProperty("currentTurn") Turn currentTurn, @JsonProperty("nextTurn") Turn nextTurn, @JsonProperty("winner") Player winner, @JsonProperty("currentRuleset") RuleSetContext currentRuleSet) {
        this.gameBoard = gameBoard;
        this.players = players;
        this.currentTurn = currentTurn;
        this.nextTurn = nextTurn;
        this.winner = winner;
        this.currentRuleSet = currentRuleSet;
    }


    /*
        this construct an exact copy of a game, used to implement undo: TODO: where should the savedGame variable be stored?
     */
    private Game(Game game) {
        this.gameBoard = game.gameBoard.cloneGameBoard();
        this.players = new ArrayList<>();
        for (Player player : game.players) {
            this.players.add(player.clonePlayer(this));
        }

        this.currentTurn = game.currentTurn.cloneTurn(this);
        this.nextTurn = game.nextTurn.cloneTurn(this);
        if (game.winner != null) {
            this.winner = this.getPlayers().stream().filter(player -> player.getName().equals(game.winner.getName())).collect(Collectors.toList()).get(0);
        } else this.winner = null;
        this.currentRuleSet = new RuleSetContext();
        currentRuleSet.setStrategy(this.currentTurn.getRuleSetStrategy());
        this.file = new File("../SavedGame2.json");

    }

    /*
        Player use this method to keep the correct references between objects: Cell and Player will have the reference to the same Worker obj instead of construct 2 different workers with equal values
     */
    public void setCellsReferences(Player player) {
        for (Worker worker : player.getWorkers()) {
            Cell tmpCell = gameBoard.getCell(worker.getPosition());
            tmpCell.setOccupiedBy(worker);
            worker.setPosition(tmpCell);
        }
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Turn currentTurn) {
        this.currentTurn = currentTurn;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public RuleSetContext getCurrentRuleSet() {
        return currentRuleSet;
    }

    public List<Cell> getWalkableCells(Worker worker) {
        return currentRuleSet.getWalkableCells(worker);
    }

    public List<Cell> getBuildableCells(Worker worker) {
        return currentRuleSet.getBuildableCells(worker);
    }

    public Player getWinner() {
        return winner;
    }

    public void validateMoveAction(MoveAction moveAction) throws IllegalActionException {
        if (currentRuleSet.validateMoveAction(moveAction)) {

            if (currentRuleSet.checkWinCondition(moveAction)) {
                this.winner = currentTurn.getCurrentPlayer();
                // TODO: manage win stuff
                endGameListener.onEndGame(winner.getName());
            }
            moveAction.apply();
            moveActionListener.onMoveAction(buildBoardData());
            this.saveState();
        } else
            throw new IllegalActionException();
    }

    public void validateBuildAction(BuildAction buildAction) throws IllegalActionException {
        if (currentRuleSet.validateBuildAction(buildAction)) {
            buildAction.apply();
            buildActionListener.onBuildAction(buildBoardData());
            this.saveState();
            endTurnAutomatically();
        } else
            throw new IllegalActionException();
    }

    public void endTurn() throws IllegalEndingTurnException {
        if (currentRuleSet.canEndTurn()) {
            generateNextTurn();
        } else
            throw new IllegalEndingTurnException();
    }

    public void endTurnAutomatically() {
        if (currentRuleSet.canEndTurnAutomatically()) {
            generateNextTurn();
        }
    }

    public Player nextPlayer() {
        if (!players.contains(getCurrentTurn().getCurrentPlayer())) {
            return players.get(((currentTurn.getTurnNumber() + 1) % players.size()));
        }
        return players.get(((players.indexOf(currentTurn.getCurrentPlayer()) + 1) % players.size()));
    }

    public void generateNextTurn() {
        nextTurn = new Turn(currentTurn.getTurnNumber() + 1, nextPlayer());
        currentRuleSet.setStrategy(currentTurn.getCurrentPlayer().getGod().getStrategy());
        currentRuleSet.doEffect();
        currentRuleSet.setStrategy(nextPlayer().getGod().getStrategy());
        currentTurn = nextTurn;

        endTurnListener.onTurnEnd(currentTurn.getCurrentPlayer().getName());
        if (currentRuleSet.checkLoseCondition()) {
            removePlayer(currentTurn.getCurrentPlayer());
        }
        this.saveState();
    }

    private void removePlayer(Player player) {
        for (Worker worker : player.getWorkers()) {
            worker.getPosition().setOccupiedBy(null);
            worker.setPosition(null);
        }
        players.remove(player);
        playerLostListener.onPlayerLoss(buildBoardData());


        if (players.size() == 1) {
            this.winner = players.get(0);
            endGameListener.onEndGame(this.winner.getName());
        }
        generateNextTurn();

    }

    /*
        this method return a gameData object built from the Game obj, this is needed to forward only the information strictly needed for the view to work as intended.
        the same applies for all the other buildDataClass methods.
        GameBoard doesn't need a dataClass, we only need the Cell matrix.
        Worker and Cell are dataclasses already
     */

    @Override
    public GameData buildGameData() {
        List<Cell> gameboardData = gameBoard.cloneAllCells();
        List<PlayerData> playersData = new ArrayList<>();
        this.players.forEach(player -> playersData.add(player.buildDataClass()));
        TurnData currentTurnData = currentTurn.buildDataClass();
        return new GameData(gameboardData, playersData, currentTurnData);
    }

    @Override
    public List<Cell> buildBoardData() {
        return gameBoard.cloneAllCells();
    }


    public Game saveStateToVariable() {
        return new Game(this);
    }

    public void saveState() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerFor(Game.class).withDefaultPrettyPrinter().writeValue(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Game restoreState() throws IOException {
        int x, y;
        ObjectMapper objectMapper = new ObjectMapper();
        Game restoredGame = objectMapper.readerFor(Game.class).readValue(file);
        for (Player player : restoredGame.players) {
            for (Worker worker : player.getWorkers()) {
                x = worker.getPosition().getCoordX();
                y = worker.getPosition().getCoordY();
                worker.setPosition(restoredGame.gameBoard.getCell(x, y));
                restoredGame.getGameBoard().getCell(x, y).setOccupiedBy(worker);
            }
            player.setGame(restoredGame);
        }
        return restoredGame;
    }

}
