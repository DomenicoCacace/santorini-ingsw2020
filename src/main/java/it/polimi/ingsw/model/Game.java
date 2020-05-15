package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.exceptions.IllegalEndingTurnException;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.dataClass.GameData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.model.dataClass.TurnData;
import it.polimi.ingsw.model.rules.RuleSetContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game implements GameInterface {

    private final GameBoard gameBoard;
    private final List<Player> players;
    private final RuleSetContext currentRuleSet;
    private Turn currentTurn;
    private Turn nextTurn;
    private Player winner;
    private final List<MoveActionListener> moveActionListener= new ArrayList<>();
    private final List<EndTurnListener> endTurnListener= new ArrayList<>();
    private final List<BuildActionListener> buildActionListener= new ArrayList<>();
    private final List<EndGameListener> endGameListener= new ArrayList<>();
    private final List<PlayerLostListener> playerLostListener = new ArrayList<>();

    public Game(GameBoard gameBoard, List<Player> players) {
        this.gameBoard = gameBoard;
        this.players = players;
        for (Player player : players) {
            player.setGame(this);
        }
        //just for testing
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
        this construct an exact copy of a game, used to implement undo:
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
        for (Worker worker : currentTurn.getCurrentPlayer().getWorkers()) {
            if (worker.getPosition().equals(moveAction.getTargetWorker().getPosition()))
                targetWorker = worker;
        }
        moveAction = new MoveAction(targetWorker, targetCell);

        if (currentRuleSet.validateMoveAction(moveAction)) {
            moveAction.apply();
            moveActionListener.forEach(moveActionListener1 -> moveActionListener1.onMoveAction(buildBoardData()));
            if(currentRuleSet.checkLoseCondition(moveAction))
                removePlayer(currentTurn.getCurrentPlayer());
            else {
                if (currentRuleSet.checkWinCondition(moveAction)) {
                    this.winner = currentTurn.getCurrentPlayer();
                    endGameListener.forEach(endGameListener1 -> endGameListener1.onEndGame(winner.getName()));
                }
            }
        } else
            throw new IllegalActionException();
    }

    public void validateBuildAction(BuildAction buildAction) throws IllegalActionException {
        Cell targetCell = gameBoard.getCell(buildAction.getTargetCell());
        Worker targetWorker = null;
        for (Worker worker : currentTurn.getCurrentPlayer().getWorkers()) {
            if (worker.getPosition().equals(buildAction.getTargetWorker().getPosition()))
                targetWorker = worker;
        }

        buildAction = new BuildAction(targetWorker, targetCell, buildAction.getTargetBlock());
        if (currentRuleSet.validateBuildAction(buildAction)) {
            buildAction.apply();
            buildActionListener.forEach(buildActionListener1 -> buildActionListener1.onBuildAction(buildBoardData()));
            if(currentRuleSet.checkLoseCondition(buildAction))
                removePlayer(currentTurn.getCurrentPlayer());
            else endTurnAutomatically();
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
        if (!players.contains(getCurrentTurn().getCurrentPlayer())) { //This happens only in a 3 player match
            int nextPlayerIndex = ((currentTurn.getTurnNumber()) % 3);
            if(nextPlayerIndex!=0)
                nextPlayerIndex--;
            return players.get(nextPlayerIndex);
        }
        return players.get(((players.indexOf(currentTurn.getCurrentPlayer()) + 1) % players.size()));
    }

    public void generateNextTurn() {
        nextTurn = new Turn(currentTurn.getTurnNumber() + 1, nextPlayer());
        currentRuleSet.setStrategy(currentTurn.getCurrentPlayer().getGod().getStrategy());
        currentRuleSet.doEffect();
        currentRuleSet.setStrategy(nextPlayer().getGod().getStrategy());
        currentTurn.getCurrentPlayer().resetSelectedWorker();
        currentTurn = nextTurn;

        if (currentRuleSet.checkLoseCondition()) {
            removePlayer(currentTurn.getCurrentPlayer());
        }
        else endTurnListener.forEach(endTurnListener1 -> endTurnListener1.onTurnEnd(currentTurn.getCurrentPlayer().getName()));
    }

    @Override
    public boolean hasFirstPlayerLost() {
        if (currentRuleSet.checkLoseCondition()){
            removePlayer(currentTurn.getCurrentPlayer());
            return true;
    }
        return false;
    }

    private void removePlayer(Player player) {
        for (Worker worker : player.getWorkers()) {
            worker.getPosition().setOccupiedBy(null);
            worker.setPosition(null);
        }
        players.remove(player);
        if (players.size() == 1) {
            this.winner = players.get(0);
            endGameListener.forEach(endGameListener1 -> endGameListener1.onEndGame(this.winner.getName()));
            return;
        }
        else {
            playerLostListener.forEach(listener -> listener.onPlayerLoss(player.getName(), buildBoardData()));
        }
        generateNextTurn(); //FIXME: we should do this only if there wasn't a winner
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

    @Override
    public void restoreState() {
        //FIXME: manage file path
        for (Player player : this.players) {
            player.setGame(this);
            int x, y;
            for (Worker worker : player.getWorkers()) {
                x = worker.getPosition().getCoordX();
                y = worker.getPosition().getCoordY();
                worker.setPosition(this.gameBoard.getCell(x, y));
                this.getGameBoard().getCell(x, y).setOccupiedBy(worker);
            }
        }
    }
    @Override
    public void addMoveActionListener(MoveActionListener moveActionListener) {
        this.moveActionListener.add(moveActionListener);
    }

    @Override
    public void addEndTurnListener(EndTurnListener endTurnListener) {
        this.endTurnListener.add(endTurnListener);
    }

    @Override
    public void addBuildActionListener(BuildActionListener buildActionListener) {
        this.buildActionListener.add(buildActionListener);
    }

    @Override
    public void addEndGameListener(EndGameListener endGameListener) {
        this.endGameListener.add(endGameListener);
    }

    @Override
    public void addPlayerLostListener(PlayerLostListener playerLostListener) {
        this.playerLostListener.add(playerLostListener);
    }

}
