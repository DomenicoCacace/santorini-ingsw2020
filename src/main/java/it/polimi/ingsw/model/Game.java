package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.exceptions.IllegalEndingTurnException;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.dataClass.GameData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.model.dataClass.TurnData;
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
    private EndGameListener endGameListener;
    private PlayerLostListener playerLostListener;

    private File file = new File("../SavedGame.json");

    public Game(GameBoard gameBoard, List<Player> players) {
        this.gameBoard = gameBoard;
        this.players = players;
        for (Player player : players)
            player.setGame(this);
        //just for testing
        this.saveState();
        currentTurn = new Turn(1, players.get(0));
        currentRuleSet = new RuleSetContext();
        currentRuleSet.setStrategy(players.get(0).getGod().getStrategy());
    }

    /*
        This is needed by jackson in order to correctly deserialize the .json where an old state of Game is stored, we need this to implement Persistence
     */
    private Game(@JsonProperty("gameBoard") GameBoard gameBoard, @JsonProperty("players") List<Player> players, @JsonProperty("currentTurn") Turn currentTurn, @JsonProperty("nextTurn") Turn nextTurn, @JsonProperty("winner") Player winner, @JsonProperty("currentRuleset") RuleSetContext currentRuleSet) {
        this.gameBoard = gameBoard;
        this.players = players;
        for (Player player : this.players)
            //setCellsReferences(player);
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
        Cell targetCell = gameBoard.getCell(moveAction.getTargetCell());
        Worker targetWorker = null;
        for (Worker worker: currentTurn.getCurrentPlayer().getWorkers()){
            if(worker.getPosition().equals(moveAction.getTargetWorker().getPosition()))
                targetWorker = worker;
        }
        moveAction= new MoveAction(targetWorker, targetCell);

        if (currentRuleSet.validateMoveAction(moveAction)) {
            moveAction.apply();
            if(moveActionListener!=null)
                moveActionListener.onMoveAction(buildBoardData());
             //TODO:moved before the checkWinCondition, check if this breaks something
            if (currentRuleSet.checkWinCondition(moveAction)) {
                this.winner = currentTurn.getCurrentPlayer();
                // TODO: manage win stuff
                if(endGameListener!=null) {
                    endGameListener.onEndGame(winner.getName());
                }
            }
            this.saveState();
        } else
            throw new IllegalActionException();
    }

    public void validateBuildAction(BuildAction buildAction) throws IllegalActionException {
        Cell targetCell = gameBoard.getCell(buildAction.getTargetCell());
        Worker targetWorker = null;
        for (Worker worker: currentTurn.getCurrentPlayer().getWorkers()){
            if(worker.getPosition().equals(buildAction.getTargetWorker().getPosition()))
                targetWorker = worker;
        }

        buildAction= new BuildAction(targetWorker, targetCell, buildAction.getTargetBlock());
        if (currentRuleSet.validateBuildAction(buildAction)) {
            buildAction.apply();
            if(buildActionListener!=null)
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

        if(endTurnListener!=null)
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

        if(playerLostListener!=null)
            playerLostListener.onPlayerLoss(player.getName());


        if (players.size() == 1) {
            this.winner = players.get(0);
            if(endGameListener!=null)
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
        //FIXME: manage file path
        for (Player player : restoredGame.players) {
            /*for (Worker worker : player.getWorkers()) { //TODO: we should do the opposite, use the occupiedBy in cell to set the position in Worker
                x = worker.getPosition().getCoordX();
                y = worker.getPosition().getCoordY();
                worker.setPosition(restoredGame.gameBoard.getCell(x, y));
                restoredGame.getGameBoard().getCell(x, y).setOccupiedBy(worker);
            }
             */
            player.setGame(restoredGame);
        }
        return restoredGame;
    }

    public void setMoveActionListener(MoveActionListener moveActionListener) {
        this.moveActionListener = moveActionListener;
    }

    public void setEndTurnListener(EndTurnListener endTurnListener) {
        this.endTurnListener = endTurnListener;
    }

    public void setBuildActionListener(BuildActionListener buildActionListener) {
        this.buildActionListener = buildActionListener;
    }

    public void setEndGameListener(EndGameListener endGameListener) {
        this.endGameListener = endGameListener;
    }

    public void setPlayerLostListener(PlayerLostListener playerLostListener) {
        this.playerLostListener = playerLostListener;
    }

}
