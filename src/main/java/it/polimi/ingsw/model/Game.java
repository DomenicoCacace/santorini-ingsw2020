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

/**
 * Provides methods to get and alter the state of the game.
 * <p>
 * In particular, a Game object:
 *     <ul>
 *         <li>Holds and changes the status of the game, composed by the {@link #players} list, the {@link #gameBoard}
 *         status, the {@link #currentTurn} and {@link #currentRuleSet};</li>
 *         <li>Checks the requests coming from the Controller and, if no errors are detected, performs the required
 *         action (see {@link it.polimi.ingsw.model.action.Action});</li>
 *         <li>Notifies the Controller about the outcome of the operations, using the various listeners;
 *         <li>Saves and restores a previous state from file, in case of a server failure (persistence)</li>
 *     </ul>
 * <p>
 *    This is perhaps the most dense class of the entire project, but after some discussion it has been decided not to
 *    "tear it apart".
 */
//TODO: create general interface Listener
public class Game implements GameInterface {

    private final GameBoard gameBoard;
    private final List<Player> players;
    private final RuleSetContext currentRuleSet;
    private final List<MoveActionListener> moveActionListener = new ArrayList<>();
    private final List<EndTurnListener> endTurnListener = new ArrayList<>();
    private final List<BuildActionListener> buildActionListener = new ArrayList<>();
    private final List<EndGameListener> endGameListener = new ArrayList<>();
    private final List<PlayerLostListener> playerLostListener = new ArrayList<>();
    private Turn currentTurn;
    private Turn nextTurn;
    private Player winner;

    /**
     * Default constructor
     * <p>
     * Creates a new Game instance with the given parameters; the game board can be empty, since Workers can be added
     * later; the Players list must not be empty and contain 2 or 3 elements (the official Santorini game allows
     * 4 players, but since it messes up with some stuff we do not implement this mode).
     *
     * @param gameBoard the game field; can be empty
     * @param players   the list of players
     */
    //used to save the game status
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

    /**
     * Jackson Constructor
     * <p>
     * Used when restoring a previously saved state from a file; creates a new Game object having the given
     * attribute values.
     *
     * @param gameBoard      the gameBoard to restore
     * @param players        the players to restore
     * @param currentTurn    the turn to resume from
     * @param nextTurn       the next turn to be played
     * @param winner         the winner, in case of a server failure at the end of the game
     * @param currentRuleSet the current ruleSet
     */
    private Game(@JsonProperty("gameBoard") GameBoard gameBoard, @JsonProperty("players") List<Player> players,
                 @JsonProperty("currentTurn") Turn currentTurn, @JsonProperty("nextTurn") Turn nextTurn,
                 @JsonProperty("winner") Player winner, @JsonProperty("currentRuleset") RuleSetContext currentRuleSet) {
        this.gameBoard = gameBoard;
        this.players = players;
        this.currentTurn = currentTurn;
        this.nextTurn = nextTurn;
        this.winner = winner;
        this.currentRuleSet = currentRuleSet;
    }

    /**
     * Copy constructor
     * <p>
     * Creates a clone of the given game; used to save the game state in {@link #saveStateToVariable()}, to
     * implement the undo functionality.
     *
     * @param game the object to clone
     */
    private Game(Game game) {
        this.gameBoard = game.gameBoard.cloneGameBoard();
        this.players = new ArrayList<>();
        for (Player player : game.players) {
            this.players.add(player.clonePlayer(this));
        }
        this.currentTurn = game.currentTurn.cloneTurn(this);
        this.nextTurn = game.nextTurn.cloneTurn(this);

        /*
         * Saving the winner, to be able to restore the game even in the (unlikely) case of a server failure exactly
         * when declaring the winner
         */
        if (game.winner != null)
            this.winner = this.getPlayers().stream().filter(player -> player.getName().equals(game.winner.getName()))
                    .collect(Collectors.toList()).get(0);
        else
            this.winner = null;

        this.currentRuleSet = new RuleSetContext();
        //FIXME: maybe this.currentRuleSet.setStrategy, would make more sense tbh
        currentRuleSet.setStrategy(this.currentTurn.getRuleSetStrategy());

    }


    /**
     * <i>currentTurn</i> getter
     *
     * @return the current turn
     */
    public Turn getCurrentTurn() {
        return currentTurn;
    }

    /**
     * <i>currentTurn</i> setter
     *
     * @param currentTurn the current turn to set
     */
    public void setCurrentTurn(Turn currentTurn) {
        this.currentTurn = currentTurn;
    }

    /**
     * <i>players</i> getter
     *
     * @return a list containing the players currently playing
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * <i>gameBoard</i> getter
     *
     * @return the gameBoard
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * <i>currentRuleSet</i> getter
     *
     * @return the current {@link RuleSetContext}
     */
    public RuleSetContext getCurrentRuleSet() {
        return currentRuleSet;
    }

    /**
     * Provides the cells the given worker can walk to
     * <p>
     * May change during the turn due to other actions.
     *
     * @param worker the worker to be moved
     * @return a list of cells the given worker can move to
     */
    public List<Cell> getWalkableCells(Worker worker) {
        return currentRuleSet.getWalkableCells(worker);
    }

    /**
     * Provides the cell the given worker can build on
     * <p>
     * May change during the turn due to other actions
     *
     * @param worker the worker willing to build
     * @return a list of cells the given worker can build on
     */
    public List<Cell> getBuildableCells(Worker worker) {
        return currentRuleSet.getBuildableCells(worker);
    }


    /**
     * <i>winner</i> getter
     *
     * @return the Player who has been declared winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Sets the position references for the Player's workers and the gameBoard cells
     *
     * @param player the player to set the references to
     */
    public void setCellsReferences(Player player) {
        for (Worker worker : player.getWorkers()) {
            Cell tmpCell = gameBoard.getCell(worker.getPosition());
            tmpCell.setOccupiedBy(worker);
            worker.setPosition(tmpCell);
        }
    }


    /**
     * Checks if a movement action is valid and eventually applies it
     * <p>
     * This method makes a call to the current ruleSet context containing the current turn strategy to check if,
     * following the current ruleSet, the provided moveAction is legal; this method DOES NOT check whether the
     * action is performed by a current player's work or not: this kind of checks should be made in the controller
     * section.
     * <br>
     * If the action is valid, it is also applied on the gameBoard, and a clone of the modified board is sent to
     * all the players through observers. In case the player won, it is also notified via the observers.
     * At the end of those operations, the game state is saved, to allow a possible undo.
     * <br>
     * If the action is not valid, an exception is thrown.
     *
     * @param moveAction the movement action to validate
     * @throws IllegalActionException if the action is not valid
     */
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
            if (currentRuleSet.checkLoseCondition(moveAction))
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

    /**
     * Checks if a building action is valid and eventually applies it
     * <p>
     * This method makes a call to the current ruleSet context containing the current turn strategy to check if,
     * following the current ruleSet, the provided buildAction is legal; this method DOES NOT check whether the
     * action is performed by a current player's work or not: this kind of checks should be made in the controller
     * section.
     * <br>
     * If the action is valid, it is also applied on the gameBoard, and the client receives a list of updated
     * cells, via listener. The game state is then saved, to allow a possible undo.
     * <br>
     * If the action is not valid, an exception is thrown.
     *
     * @param buildAction the movement action to validate
     * @throws IllegalActionException if the action is not valid
     */
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
            if (currentRuleSet.checkLoseCondition(buildAction))
                removePlayer(currentTurn.getCurrentPlayer());
            else endTurnAutomatically();
        } else
            throw new IllegalActionException();
    }


    /**
     * Ends the turn upon a request
     * <p>
     * The condition for a player to end their turn may vary based on their chosen god, so this method calls the
     * current ruleSet method to check if the player can end the turn. If all the conditions for the turn to end are
     * verified, the next turn is generated (see {@linkplain #generateNextTurn()});
     * otherwise, an exception is thrown.
     *
     * @throws IllegalEndingTurnException if the player cannot end their turn
     */
    public void endTurn() throws IllegalEndingTurnException {
        if (currentRuleSet.canEndTurn())
            generateNextTurn();
        else
            throw new IllegalEndingTurnException();
    }

    /**
     * Terminates the current turn, if possible
     * <p>
     * As its twin {@linkplain #endTurn()}, this method, if successful, ends the current player turn.
     * <br>
     * The conditions to end the turn may vary based on the player's chosen god; generally, those conditions
     * require the player to have spent all the available actions for its current turn, so the turn is
     * automatically ended.
     */
    /*
     * TODO: when implementing the undo function, verify that the player is allowed to undo its last action before the
     *  next turn is generated, as it might cause severe problems
     *  (we might never implement this feature)
     */
    private void endTurnAutomatically() {
        if (currentRuleSet.canEndTurnAutomatically())
            generateNextTurn();
    }

    /**
     * Generates the next game turn
     * <p>
     * Creates a new {@linkplain Turn} object, applies the end turn effects for the current player, sets the next
     * player's ruleSet strategy, then actually set the new turn
     * <br>
     * When the new turn begins, the listeners are notified; the lose conditions are then verified, before the player
     * can make any actions; if verified, the player is removed.
     */
    public void generateNextTurn() {
        nextTurn = new Turn(currentTurn.getTurnNumber() + 1, nextPlayer());
        //FIXME: line below seems to be redundant
        currentRuleSet.setStrategy(currentTurn.getCurrentPlayer().getGod().getStrategy());
        currentRuleSet.doEffect();
        currentRuleSet.setStrategy(nextPlayer().getGod().getStrategy());
        currentTurn.getCurrentPlayer().resetSelectedWorker();
        currentTurn = nextTurn;

        if (currentRuleSet.checkLoseCondition()) {
            removePlayer(currentTurn.getCurrentPlayer());
        } else
            endTurnListener.forEach(endTurnListener1 -> endTurnListener1.onTurnEnd(currentTurn.getCurrentPlayer().getName()));
    }

    /**
     * Checks if the current player can start their turn
     *
     * @return true if the player can start the turn, false otherwise
     */
    @Override
    public boolean hasFirstPlayerLost() {
        if (currentRuleSet.checkLoseCondition()) {
            removePlayer(currentTurn.getCurrentPlayer());
            return true;
        }
        return false;
    }

    /**
     * Calculates player playing next
     * <p>
     * By default, the next player is calculated by getting the current player's index in the {@linkplain #players}
     * list and adding 1 (modulus the number of players).
     * <br>
     * This formula stops working in case a player is removed
     * from the game; in this case, we use the turn number to calculate the next player.
     *
     * @return the next player
     */
    public Player nextPlayer() {
        if (!players.contains(getCurrentTurn().getCurrentPlayer())) { //This happens only in a 3 player match
            int nextPlayerIndex = ((currentTurn.getTurnNumber()) % 3);
            if (nextPlayerIndex != 0)
                nextPlayerIndex--;
            return players.get(nextPlayerIndex);
        }
        return players.get(((players.indexOf(currentTurn.getCurrentPlayer()) + 1) % players.size()));
    }

    /**
     * Removes a player from the game
     * <p>
     * Removes the player's workers from the board, then removes the player from the players list.
     * <br>
     * If, after the deletion only one player is left it is declared winner, and the relative listener is
     * notified.
     *
     * @param player the player to remove from the game
     */
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
        } else {
            playerLostListener.forEach(listener -> listener.onPlayerLoss(player.getName(), buildBoardData()));
        }
        generateNextTurn(); //FIXME: we should do this only if there wasn't a winner
    }


    /**
     * Creates a {@linkplain GameData} object using this object's information
     *
     * @return this object's data class
     */
    @Override
    public GameData buildGameData() {
        List<Cell> gameBoardData = gameBoard.cloneAllCells();
        List<PlayerData> playersData = new ArrayList<>();
        this.players.forEach(player -> playersData.add(player.buildDataClass()));
        TurnData currentTurnData = currentTurn.buildDataClass();
        return new GameData(gameBoardData, playersData, currentTurnData);
    }

    /**
     * Clones the game's gameBoard as a list of cells
     *
     * @return a clone of the gameBoard
     */
    @Override
    public List<Cell> buildBoardData() {
        return gameBoard.cloneAllCells();
    }


    /**
     * Creates a clone of this object
     *
     * @return a clone of this
     */
    public Game saveStateToVariable() {
        return new Game(this);
    }


    /**
     * Restores the game to a previously saved state
     * <p>
     * To work, the file containing the saved game must be already set
     *
     * @see it.polimi.ingsw.network.server.Lobby#reloadMatch(boolean)
     */
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

    /**
     * Adds a new {@linkplain MoveActionListener} to the corresponding list
     *
     * @param moveActionListener the listener to add
     */
    @Override
    public void addMoveActionListener(MoveActionListener moveActionListener) {
        this.moveActionListener.add(moveActionListener);
    }

    /**
     * Adds a new {@linkplain EndTurnListener} to the corresponding list
     *
     * @param endTurnListener the listener to add
     */
    @Override
    public void addEndTurnListener(EndTurnListener endTurnListener) {
        this.endTurnListener.add(endTurnListener);
    }

    /**
     * Adds a new {@linkplain BuildActionListener} to the corresponding list
     *
     * @param buildActionListener the listener to add
     */
    @Override
    public void addBuildActionListener(BuildActionListener buildActionListener) {
        this.buildActionListener.add(buildActionListener);
    }

    /**
     * Adds a new {@linkplain EndGameListener} to the corresponding list
     *
     * @param endGameListener the listener to add
     */
    @Override
    public void addEndGameListener(EndGameListener endGameListener) {
        this.endGameListener.add(endGameListener);
    }

    /**
     * Adds a new {@linkplain PlayerLostListener} to the corresponding list
     *
     * @param playerLostListener the listener to add
     */
    @Override
    public void addPlayerLostListener(PlayerLostListener playerLostListener) {
        this.playerLostListener.add(playerLostListener);
    }

}
