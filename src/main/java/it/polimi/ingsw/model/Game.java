package it.polimi.ingsw.model;

import java.util.List;

public class Game {
    private Turn currentTurn;
    private Turn nextTurn;
    private GameBoard gameBoard;
    private Player winner;
    private List<Player> players;
    private RuleSetContext currentRuleSet;

    public Game(GameBoard gameBoard, List<Player> players) {
        this.gameBoard = gameBoard;
        this.players = players;
        for(Player player : players) {
            player.setGame(this);
        }
        //just for testing
        currentRuleSet = new RuleSetContext();
        currentRuleSet.setStrategy(new RuleSetBase());
    }

    public Turn getCurrentTurn() { return currentTurn; }

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

    public void apply(Action action){
        action.applier();
    }

    public void validateAction(Action action){
        switch (action.getType()){
            case MOVE:
                if(currentRuleSet.validateMoveAction(action))
                    apply(action);
                break;
            case BUILD:
                if(currentRuleSet.validateBuildAction(action))
                    apply(action);
                break;
            default: break;
        }
    }

    public void resetTurn(){
        // da definire !!!
    }
    public void endTurn(){
        // da definire !!!
    }
    public Turn generateNextTurn(){
        // da definire !!!
        return null;
    }
    public void applyEndTurnEffects(){
        // da definire !!!
    }

}
