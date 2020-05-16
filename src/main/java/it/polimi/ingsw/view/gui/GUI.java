package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GUI extends Application implements ViewInterface {

    private String inputString = "";
    private int inputInteger = 0;
    boolean updated = false;
    private final Lock lock = new ReentrantLock();
    private final Condition unlockCondition = lock.newCondition();


    @Override
    public void start(Stage primarystage) throws IOException {
        Parent panel;
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("LoginResources/santorini.fxml"));
        System.out.println(GUI.class.getResource("LoginResources/santorini.fxml"));
        panel = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        loginController.setGUI(this);
        Scene scene = new Scene(panel);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        new Thread(() -> Client.initClient(this)).start();
    }

    public static void launchGui(){
        launch();
    }



    public void setInputString(String inputString) {
        if (!inputString.equals("")) {
            lock.lock();
            try {
                this.inputString = inputString;
                updated = true;
                unlockCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void setInputInteger(int inputInteger) {
        this.inputInteger = inputInteger;
    }

    @Override
    public void stopInput() {

    }

    @Override
    public List<String> askToReloadLastSettings(List<String> savedUsers) throws TimeoutException, InterruptedException {
        return new ArrayList<>();
    }

    @Override
    public boolean chooseMatchReload() throws TimeoutException, InterruptedException {
        return false;
    }

    @Override
    public void printLogo() {
    }

    @Override
    public String askIP() {
        updated = false;
        lock.lock();
        try {
            while (!updated)
                unlockCondition.await();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lock.unlock();
        String tmp = inputString;
        inputString = "";
        return tmp;
    }

    @Override
    public String askUsername() {
        updated=false;
        lock.lock();
        try {
            while (!updated)
                unlockCondition.await();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lock.unlock();
        String tmp = inputString;
        inputString = "";
        return tmp;
    }

    @Override
    public void gameStartScreen(List<Cell> gameBoard) {

    }

    @Override
    public String lobbyOptions(List<String> options) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public String askLobbyName() throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public int askLobbySize() throws TimeoutException, InterruptedException {
        return 0;
    }

    @Override
    public String chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public Cell chooseWorker() throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public Block chooseBlockToBuild(List<Block> buildableBlocks) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public GodData chooseUserGod(List<GodData> possibleGods) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public List<GodData> chooseGameGods(List<GodData> allGods, int size) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public String chooseStartingPlayer(List<String> players) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public Cell placeWorker() throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public PossibleActions chooseAction(List<PossibleActions> possibleActions) throws TimeoutException, InterruptedException {
        return null;
    }

    @Override
    public void showGameBoard(List<Cell> gameBoard) {

    }

    @Override
    public void initGameBoard(List<Cell> gameboard) {

    }

    @Override
    public void showErrorMessage(String error) {

    }

    @Override
    public void showSuccessMessage(String message) {

    }
}
