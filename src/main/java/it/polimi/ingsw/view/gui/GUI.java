package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.gui.viewController.ChooseGodsController;
import it.polimi.ingsw.view.gui.viewController.LobbyController;
import it.polimi.ingsw.view.gui.viewController.LoginController;
import it.polimi.ingsw.view.inputManagers.InputManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GUI extends Application implements ViewInterface {

    private static Scene scene;
    private ViewsType currentView;
    private String inputString = "";
    private static final Lock lock = new ReentrantLock();
    private InputManager inputManager;


    @Override
    public void start(Stage primarystage) {
        Parent empty = new Pane();
        scene = new Scene(empty, 1280, 720);
        primarystage.setScene(scene);
        primarystage.show();
        primarystage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
        new Thread(() -> Client.initClient(this)).start();
    }

    public static void setRoot(String fxml) {
        lock.lock();
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
        scene.setUserData(fxmlLoader);
        try {
            scene.setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }


    public static void launchGui() {
        launch();
    }

    public void setInputString(String inputString) {
        lock.lock();
        inputManager.manageInput(inputString);
        lock.unlock();
    }

    @Override
    public void askToReloadLastSettings(List<String> savedUsers) {
        Platform.runLater(() -> {
            LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
            if (savedUsers.size() > 0) {
                for (int i = 1; i <= savedUsers.size(); i += 2) {
                    loginController.getOldConfigs().getItems().add(savedUsers.get(i) + " -- " + savedUsers.get(i - 1)); //This convert
                }
                loginController.getOldConfigs().setOpacity(1);
                loginController.getOldConfigs().setDisable(false);
                loginController.setReloading(true);
            }
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getIpID().setOpacity(1);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getIpID().setDisable(false);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setOpacity(1);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setDisable(false);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setOpacity(1);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setDisable(false);
        });
    }

    @Override
    public void chooseMatchReload() {

    }

    @Override
    public void setInputManager(InputManager manager) {
        this.inputManager = manager;
    }

    @Override
    public void printLogo() {
        Platform.runLater(() -> {
            setRoot("LoginResources/santorini");
            LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
            loginController.setGUI(this);
            currentView = ViewsType.LOGIN;
        });
    }

    @Override
    public void askIP() {
        Platform.runLater(() -> {
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getIpID().setOpacity(1);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getIpID().setDisable(false);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setOpacity(1);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setDisable(false);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setOpacity(1);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setDisable(false);
        });
    }

    @Override
    public void askUsername() {
        Platform.runLater(() -> {
            if (currentView != ViewsType.LOGIN) {
                printLogo();
                LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
                loginController.setIpIsSet(true);
            }
            LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
            loginController.getUsernameID().setOpacity(1);
            loginController.getUsernameID().setDisable(false);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setOpacity(1);
            ((LoginController) ((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setDisable(false);
        });
    }

    @Override
    public void gameStartScreen(List<Cell> gameBoard) {

    }

    @Override
    public void lobbyOptions(List<String> options) {
        Platform.runLater(() -> {
            setRoot("LoginResources/lobbyView");
            LobbyController lobbyController = ((FXMLLoader) scene.getUserData()).getController();
            lobbyController.setGUI(this);
            if (options.size() == 1) {
                lobbyController.getJoinBtn().setDisable(true);
            }
            currentView = ViewsType.LOBBY;
            LobbyController.setInputRequested(true);
        });
    }

//TODO: Ask tutor about this
    @Override
    public void askLobbyName() {

    }

    @Override
    public void askLobbySize() {

    }


    @Override
    public void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) {
        Platform.runLater(() -> {
            currentView = ViewsType.LOBBY;
            setRoot("LoginResources/chooseLobby");
            LobbyController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.getLobbyList().getItems().addAll(lobbiesAvailable.keySet());
            controller.setGUI(this);
            LobbyController.setInputRequested(true);
        });
    }

    @Override
    public void chooseWorker() {

    }

    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {

    }

    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {

    }

    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) {

    }

    @Override
    public void chooseUserGod(List<GodData> possibleGods) {

    }

    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {
        Platform.runLater(() -> {
            setRoot("chooseGods");
            ChooseGodsController controller = ((FXMLLoader) scene.getUserData()).getController();
            List<VBox> vBoxes = new ArrayList<>();
            controller.getGodImageContainer().getChildren().forEach(vBox -> vBoxes.add((VBox)vBox));
            List<Node> vboxChildren = new ArrayList<>();
            vBoxes.forEach(vBox -> vboxChildren.addAll(vBox.getChildren()));
            for(Node node: vboxChildren){
                for(GodData godData: allGods){
                    if(godData.getName().equals(node.getId())){
                        ((ResizableImageView)node).setIndex(allGods.indexOf(godData));
                    }
                }
            }
            controller.setGUI(this);
        });
    }

    @Override
    public void chooseStartingPlayer(List<String> players) {

    }

    @Override
    public void placeWorker() {

    }

    @Override
    public void chooseAction(List<PossibleActions> possibleActions) {

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
    public void printOptions(List<String> options) {

    }

    @Override
    public void showSuccessMessage(String message) {

    }


}
