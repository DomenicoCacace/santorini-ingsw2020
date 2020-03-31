package it.polimi.ingsw.model;

import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.RuleSetContext;

import java.util.List;

public class Game {
    private final GameBoard gameBoard;
    private final List<Player> players;
    private Turn currentTurn;
    private Turn nextTurn;
    private Player winner;
    private RuleSetContext currentRuleSet;

    public Game(GameBoard gameBoard, List<Player> players) {
        this.gameBoard = gameBoard;
        this.players = players;
        for (Player player : players)
            player.setGame(this);

        //just for testing
        currentRuleSet = new RuleSetContext();
        //currentRuleSet.setStrategy(new RuleSetBase());
        //currentRuleSet.setGame(this); //linea aggiunta per il test di getWalkable -- questa operazione è solo per il testing
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

    public  List<Cell> getBuildableCells(Worker worker) { return currentRuleSet.getBuildableCells(worker); }

    public Player getWinner() {
        return winner;
    }

    public void apply(Action action) {
        action.applier();
    }

    public void validateAction(Action action) {

        switch (action.getType()) {

            case MOVE:
                if (currentRuleSet.validateMoveAction(action)) {

                    if (currentRuleSet.checkWinCondition(action)) {
                        this.winner = currentTurn.getCurrentPlayer();
                        //TODO: manage win stuff
                    } else if (currentRuleSet.checkLoseCondition(action)) {
                        //TODO: manage lose stuff
                    }
                    apply(action);
                }
                break;

            case BUILD:
                if (currentRuleSet.validateBuildAction(action))
                    apply(action);
                    //TODO: might need to check win/lose condition for certain gods
                break;

            default:
                break;
        }
    }

    public Player nextPlayer() {
        return players.get(((players.indexOf(currentTurn.getCurrentPlayer()) + 1) % players.size()));
    }

    public void generateNextTurn() {
        nextTurn = new Turn(currentTurn.getTurnNumber() + 1, nextPlayer());

        for (Player player : players) {
            currentRuleSet.setStrategy(player.getGod().getStrategy());
            currentRuleSet.doEffect(nextTurn);
        }

        currentRuleSet.setStrategy(nextPlayer().getGod().getStrategy());
        currentTurn = nextTurn;
    }
    // savedTurn = game.currentTurn.saveState(); controller will save the state in this way
}
