package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.gui.utils.MapTileImage;
import it.polimi.ingsw.view.gui.utils.ResizableImageView;
import it.polimi.ingsw.view.gui.viewController.*;
import it.polimi.ingsw.view.inputManagers.InputManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GUI extends Application implements ViewInterface {
    private enum ViewType{
        LOGIN, LOBBY, GAME
    }

    private static final Lock lock = new ReentrantLock();
    private static Scene scene;
    private final String inputString = "";
    private ViewType currentView;
    private final Condition condition = lock.newCondition();
    private InputManager inputManager;

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
       /* Platform.runLater(()->{
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(scene.getWindow());
            Pane pane = new Pane();

            Popup popup = new Popup();
            Label label = new Label("I found a message to reload! Do you want to reload it?");
            Button yesButton = new Button("YES");
            yesButton.setOnMouseClicked(event -> inputManager.manageInput(CLI.YES));
            Button noButton = new Button("NO");
            noButton.setOnMouseClicked(event -> inputManager.manageInput(CLI.NO));
            pane.getChildren().addAll(label, yesButton, noButton);
            Scene dialogScene = new Scene(pane, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
            //popup.getContent().addAll(label, yesButton, noButton);
            //popup.show(scene.getWindow());
        });
        */
        inputManager.manageInput(CLI.YES); //TODO: Implement method to reload match
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
            currentView = ViewType.LOGIN;
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
            if (currentView != ViewType.LOGIN) {
                setRoot("LoginResources/santorini");
                LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
                loginController.setGUI(this);
                currentView = ViewType.LOGIN;
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
        //TODO: printBuilding - Print popUp
        initGameBoard(gameBoard);
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
            currentView = ViewType.LOBBY;
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
            currentView = ViewType.LOBBY;
            setRoot("LoginResources/chooseLobby");
            LobbyController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.getLobbyList().getItems().addAll(lobbiesAvailable.keySet());
            controller.setGUI(this);
            LobbyController.setInputRequested(true);
        });
    }

    @Override
    public void chooseWorker() {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.setGui(this);
            controller.allCellsClickable();
        });
    }

    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.setGui(this);
            controller.makeCellsClickable(walkableCells);
        });
    }

    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.setGui(this);
            controller.makeCellsClickable(buildableCells);
        });
    }

    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) {
        setInputString("2"); //TODO don't hardcode
    }

    @Override
    public void chooseUserGod(List<GodData> possibleGods) {
        showGodList(possibleGods);
    }

    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {
        showGodList(allGods);
    }

    private void showGodList(List<GodData> possibleGods) {
        Platform.runLater(() -> {
            setRoot("chooseGods");
            ChooseGodsController controller = ((FXMLLoader) scene.getUserData()).getController();
            List<VBox> vBoxes = new ArrayList<>();
            controller.getGodImageContainer().getChildren().forEach(vBox -> vBoxes.add((VBox) vBox));
            List<Node> vboxChildren = new ArrayList<>();
            vBoxes.forEach(vBox -> vboxChildren.addAll(vBox.getChildren()));
            for(Node node: vboxChildren){
                ListIterator<GodData> godDataListIterator = possibleGods.listIterator();
                boolean nameFound = false;
                GodData god;
                while (godDataListIterator.hasNext() && !nameFound){
                    god = godDataListIterator.next();
                    if (god.getName().equals(node.getId())) {
                        ((ResizableImageView) node).setIndex(possibleGods.indexOf(god));
                        nameFound=true;
                    }
                    else if(!godDataListIterator.hasNext()) {
                        node.setOpacity(0.2);
                        node.setDisable(true);
                    }
                }
            }
            controller.setGUI(this);
        });
    }


    @Override
    public void chooseStartingPlayer(List<String> players) {
        Platform.runLater(() -> {
            setRoot("ChooseStartingPlayer");
            StartingPlayerController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.setGui(this);
            controller.getPlayersList().getItems().addAll(players);
        });
    }

    @Override
    public void placeWorker() {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.setGui(this);
            controller.allCellsClickable();
        });
    }

    @Override
    public void chooseAction(List<PossibleActions> possibleActions) {
        setInputString("1");
    }


    @Override
    public void showGameBoard(List<Cell> gameBoard) {
        Platform.runLater(() -> {
            if (currentView != ViewType.GAME) {
                setRoot("allMap");
                currentView = ViewType.GAME;
            }
            List<MapTileImage> mapTileImages = parseMap();
            for (MapTileImage mapTile : mapTileImages) {
                int row = GridPane.getRowIndex(mapTile.getParent()) - 1;
                int col = GridPane.getColumnIndex(mapTile.getParent()) - 1;
                Cell cell = gameBoard.get(5 * row + col);
                if (cell.getBlock().getHeight() != mapTile.getHeight())
                    mapTile.printBlock(cell.getBlock().getHeight());
                boolean cellIsOccupied = cell.getOccupiedBy() != null;
                if (cellIsOccupied)
                    mapTile.printWorker(cell.getOccupiedBy().getColor());
                else if (mapTile.isOccupied())
                    mapTile.removeWorker();
            }
        });

    }

    @Override
    public void initGameBoard(List<Cell> gameBoard) {
        //TODO: printBuilding
        Platform.runLater(() -> {
            if (currentView != ViewType.GAME) {
                setRoot("allMap");
                currentView = ViewType.GAME;
            }
            List<MapTileImage> mapTileImages = parseMap();
            for (MapTileImage mapTile : mapTileImages) {
                int row = GridPane.getRowIndex(mapTile.getParent()) - 1;
                int col = GridPane.getColumnIndex(mapTile.getParent()) - 1;
                Cell cell = gameBoard.get(5 * row + col);
                if (cell.getBlock().getHeight() > 0)
                    mapTile.printBuilding(cell.getBlock().getHeight());
                boolean cellIsOccupied = cell.getOccupiedBy() != null;
                if (cellIsOccupied)
                    mapTile.printWorker(cell.getOccupiedBy().getColor());
                else if (mapTile.isOccupied())
                    mapTile.removeWorker();
            }
        });
    }

    private List<MapTileImage> parseMap() {
        GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
        List<MapTileImage> mapTileImages = new ArrayList<>();
        controller.getMapGrid().getChildren()
                .forEach(node -> ((StackPane) node).getChildren()
                        .forEach(mapTile -> {
                            if (((MapTileImage) mapTile).isCellTile())
                                mapTileImages.add((MapTileImage) mapTile);
                        }));
        return mapTileImages;
    }

    @Override
    public void showErrorMessage(String error) {
        System.out.println(error);
    }

    @Override
    public void printOptions(List<String> options) {

    }

    @Override
    public void showSuccessMessage(String message) {
        System.out.println(message);
    }


}
