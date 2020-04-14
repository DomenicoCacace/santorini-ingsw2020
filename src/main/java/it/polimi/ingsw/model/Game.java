package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.ObserverPattern.ObservableInterface;
import it.polimi.ingsw.ObserverPattern.ObserverInterface;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetContext;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Game implements ObservableInterface {
    private GameBoard gameBoard;
    private List<Player> players;
    private Turn currentTurn;
    private Turn nextTurn;
    private Player winner;
    private RuleSetContext currentRuleSet;
    private File file = new File("../SavedGame.json");

    public Game( GameBoard gameBoard,  List<Player> players) throws IOException {
        this.gameBoard = gameBoard;
        this.players = players;
        for (Player player : players)
            player.setGame(this);
        //just for testing
        currentRuleSet = new RuleSetContext();
        this.saveState();
    }

    private Game(@JsonProperty("gameBoard")GameBoard gameBoard, @JsonProperty("players") List<Player> players, @JsonProperty("currentTurn") Turn currentTurn, @JsonProperty("nextTurn") Turn nextTurn, @JsonProperty("winner") Player winner, @JsonProperty("currentRuleset") RuleSetContext currentRuleSet) {
        this.gameBoard = gameBoard;
        this.players = players;
        this.currentTurn = currentTurn;
        this.nextTurn = nextTurn;
        this.winner = winner;
        this.currentRuleSet = currentRuleSet;
    }

    private Game(Game game){ //TODO: get clone and privatize constructors
        this.gameBoard = new GameBoard(game.gameBoard);
        this.players = new ArrayList<>();
        for(Player player: game.players){
            this.players.add(new Player(player, this));
        }

        this.currentTurn = new Turn(game.currentTurn, this);
        this.nextTurn = new Turn(game.nextTurn, this);
        if(game.winner!= null) {
            this.winner = this.getPlayers().stream().filter(player -> player.getName().equals(game.winner.getName())).collect(Collectors.toList()).get(0);
        }
        else this.winner = null;
        this.currentRuleSet = new RuleSetContext();
        currentRuleSet.setStrategy(this.currentTurn.getRuleSetStrategy());
        this.file = new File("../SavedGame2.json");
    }

    public void setCellsReferences(Player player){
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

    public List<Cell> getWalkableCells(Worker worker) throws LostException, IOException {
        List<Cell> walkable = new ArrayList<>();
        try {
            walkable = currentRuleSet.getWalkableCells(worker);
        } catch (LostException le) {
            removePlayer(currentTurn.getCurrentPlayer());
        }
        return walkable;
    }

    public List<Cell> getBuildableCells(Worker worker) throws IOException, LostException {
        List<Cell> buildable = new ArrayList<>();
        try {
            buildable = currentRuleSet.getBuildableCells(worker);
        } catch (LostException le) {
            removePlayer(currentTurn.getCurrentPlayer());
        }
        return buildable;
    }

    public Player getWinner() {
        return winner;
    }

    public void validateMoveAction(MoveAction moveAction) throws IOException, LostException {
        if (currentRuleSet.validateMoveAction(moveAction)) {

            if (currentRuleSet.checkWinCondition(moveAction)) {
                this.winner = currentTurn.getCurrentPlayer();
                // TODO: manage win stuff
            }
            moveAction.apply();
            //TODO: notifyObservers(MessageEvent.PLAYER_MOVE);
            this.saveState();
        }
    }

    public void validateBuildAction(BuildAction buildAction) throws IOException, LostException {
        if (currentRuleSet.validateBuildAction(buildAction)) {
            buildAction.apply();
            //TODO: notifyObservers();
            this.saveState();
            endTurnAutomatically();
        }
    }

    public void endTurn() throws IOException, LostException {
        if(currentRuleSet.canEndTurn())
            generateNextTurn();
    }

    public void endTurnAutomatically() throws IOException, LostException {
        if(currentRuleSet.canEndTurnAutomatically())
            generateNextTurn();
    }

    public Player nextPlayer() {
        if(!players.contains(getCurrentTurn().getCurrentPlayer())){
            return players.get(((currentTurn.getTurnNumber() + 1) % players.size()));
        }
        return players.get(((players.indexOf(currentTurn.getCurrentPlayer()) + 1) % players.size()));
    }

    public void generateNextTurn() throws IOException, LostException {
        nextTurn = new Turn(currentTurn.getTurnNumber() + 1, nextPlayer());
        currentRuleSet.setStrategy(currentTurn.getCurrentPlayer().getGod().getStrategy());
        currentRuleSet.doEffect();
        currentRuleSet.setStrategy(nextPlayer().getGod().getStrategy());
        currentTurn = nextTurn;
        if(currentRuleSet.checkLoseCondition()) {
            removePlayer(currentTurn.getCurrentPlayer());
        }
        this.saveState();
    }

    private void removePlayer(Player player) throws IOException, LostException {
        for(Worker worker: player.getWorkers()){
            worker.getPosition().setOccupiedBy(null);
            worker.setPosition(null);
        }
        players.remove(player);
        if(players.size()==1){
            this.winner = players.get(0);
            //TODO: manage win
        }
        generateNextTurn();
    }

    public Game saveStateToVariable(){
        return new Game(this);
    }

    public void saveState(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerFor(Game.class).withDefaultPrettyPrinter().writeValue(file, this);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Game restoreState() throws IOException {
        int x, y;
        ObjectMapper objectMapper = new ObjectMapper();
        Game restoredGame = objectMapper.readerFor(Game.class).readValue(file);
        for(Player player: restoredGame.players){
            for(Worker worker: player.getWorkers()){
                x=worker.getPosition().getCoordX();
                y=worker.getPosition().getCoordY();
                worker.setPosition(restoredGame.gameBoard.getCell(x,y));
                restoredGame.getGameBoard().getCell(x,y).setOccupiedBy(worker);
            }
            player.setGame(restoredGame);
        }
        return restoredGame;
    }

    @Override
    public void addObserver(ObserverInterface observer, Event event) {

    }

    @Override
    public void removeObserver(ObserverInterface observer, Event event) {

    }

    @Override
    public void notifyObservers(Event event) {


    }
}
