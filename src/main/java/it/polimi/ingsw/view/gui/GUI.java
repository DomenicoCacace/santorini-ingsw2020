package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.view.ViewInterface;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GUI extends Application implements ViewInterface {
    private static final Lock lock = new ReentrantLock();
    private static Scene scene;
    private ViewType currentView;
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
        primarystage.setTitle("Santorini");
        primarystage.minHeightProperty().setValue(360);
        primarystage.minWidthProperty().setValue(640);
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
        Platform.runLater(() -> {
            setRoot("reloadMatch");
            ReloadMatchController reloadMatchController = ((FXMLLoader) scene.getUserData()).getController();
            reloadMatchController.setGUI(this);
        });
    }

    @Override
    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
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
    public void gameStartScreen(List<Cell> gameBoard, List<PlayerData> players) {
        //TODO: Print popUp
        refreshGameScreen(gameBoard, players);
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

    @Override
    public void askLobbyName() {
        /*Platform.runLater(() -> {
            if(currentView == ViewType.LOBBY) {
                LobbyController lobbyController = ((FXMLLoader) scene.getUserData()).getController();
                lobbyController.enableCreateButton();
            }
        });
         */
    }

    @Override
    public void askLobbySize() {
        Platform.runLater(() -> {
            if (currentView == ViewType.LOBBY) {
                LobbyController lobbyController = ((FXMLLoader) scene.getUserData()).getController();
                lobbyController.enableCreateButton();
            }
        });
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
    public void chooseWorker(List<Cell> workersCells) {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.getTextArea().appendText("\n\nChoose your worker!\n");
            controller.makeCellsClickable(workersCells);
        });
    }

    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.getTextArea().appendText("\nSelect the cell where you want to to move! ");
            controller.makeCellsClickable(walkableCells);
        });
    }

    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.getTextArea().appendText("\nSelect the cell where you want to to build! ");
            //controller.setGui(this);
            controller.makeCellsClickable(buildableCells);
        });
    }

    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.getTextArea().appendText("\nChoose the block to build: ");
            List<String> blockNames = new ArrayList<>();
            buildableBlocks.forEach(block -> blockNames.add(block.toString()));
            controller.getChoiceList().getItems().addAll(blockNames);
            controller.getChoiceList().setDisable(false);
        });
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
            List<HBox> hBoxes = new ArrayList<>();
            controller.getGodImageContainer().getChildren().forEach(hBox -> hBoxes.add((HBox) hBox));
            List<Node> hBoxChildren = new ArrayList<>();
            hBoxes.forEach(hBox -> hBoxChildren.addAll(hBox.getChildren()));
            for (Node node : hBoxChildren) {
                ListIterator<GodData> godDataListIterator = possibleGods.listIterator();
                boolean nameFound = false;
                GodData god;
                while (godDataListIterator.hasNext() && !nameFound) {
                    god = godDataListIterator.next();
                    if (god.getName().equals(node.getId())) {
                        controller.addEnabledGod((ResizableImageView) node);
                        ((ResizableImageView) node).setIndex(possibleGods.indexOf(god));
                        node.setDisable(false);
                        node.setOpacity(1);
                        nameFound = true;
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
            controller.getTextArea().appendText("\nPlace your Worker! ");
            controller.allCellsClickable();
        });
    }

    @Override
    public void chooseAction(List<PossibleActions> possibleActions) {
        Platform.runLater(() -> {
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            possibleActions.forEach(action -> controller.getChoiceList().getItems().add(action.toString()));
            controller.getChoiceList().setDisable(false);
        });
    }

    @Override
    public void showGameBoard(List<Cell> gameBoard) {
        Platform.runLater(() -> {
            if (currentView != ViewType.GAME) {
                setRoot("allMap");
                currentView = ViewType.GAME;
            }
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.setGui(this);
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
    public void refreshGameScreen(List<Cell> gameBoard, List<PlayerData> players) {
        Platform.runLater(() -> {
            if (currentView != ViewType.GAME) {
                setRoot("allMap");
                currentView = ViewType.GAME;
            }
            GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
            controller.setGui(this);
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
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, error + "\n", ButtonType.OK);
            alert.showAndWait();
        });
    }


    @Override
    public void showSuccessMessage(String message) {
        if (currentView.equals(ViewType.GAME)) {
            Platform.runLater(() -> {
                GameScreenController controller = ((FXMLLoader) scene.getUserData()).getController();
                controller.getTextArea().appendText(message + "\n");
            });
        } else System.out.println(message);
    }

    private enum ViewType {
        LOGIN, LOBBY, GAME
    }
}
