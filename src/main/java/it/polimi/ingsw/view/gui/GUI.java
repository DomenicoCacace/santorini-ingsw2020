package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.inputManagers.InputManager;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;


public class GUI extends Application implements ViewInterface {

    @Override
    public void askToReloadLastSettings(List<String> savedUsers) {

    }

    @Override
    public void chooseMatchReload() {

    }

    @Override
    public void setInputManager(InputManager manager) {

    }

    @Override
    public void printLogo() {

    }

    @Override
    public void askIP() {

    }

    @Override
    public void askUsername() {

    }

    @Override
    public void gameStartScreen(List<Cell> gameBoard) {

    }

    @Override
    public void lobbyOptions(List<String> options) {

    }

    @Override
    public void askLobbyName() {

    }

    @Override
    public void askLobbySize() {

    }

    @Override
    public void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) {

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

    @Override
    public void start(Stage stage) throws Exception {

    }
/*
    private static Scene scene;
    private ViewsType currentView;
    private String inputString = "";
    private int inputInteger = -1;
    private boolean inputReceived = false;
    private final Lock lock = new ReentrantLock();
    private final Condition unlockCondition = lock.newCondition();


    @Override
    public void start(Stage primarystage) {
        Parent empty = new Pane() ;
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
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
        scene.setUserData(fxmlLoader);
        try {
            scene.setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public static void launchGui(){
        launch();
    }

    public void setInputString(String inputString) {
        lock.lock();
        try {
            this.inputString = inputString;
            inputReceived = true;
            unlockCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void setInputInteger(int inputInteger) {
        lock.lock();
        try {
            this.inputInteger = inputInteger;
            inputReceived = true;
            unlockCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stopInput() {

    }

    @Override
    public List<String> askToReloadLastSettings(List<String> savedUsers) {
        List<String> chosenConfig = new ArrayList<>();
        inputReceived=false;
        lock.lock();
        try {
            LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
            if(savedUsers.size()>0) {
                for (int i = 1; i <= savedUsers.size(); i += 2) {
                    loginController.getOldConfigs().getItems().add(savedUsers.get(i) + " -- " + savedUsers.get(i - 1)); //This convert
                }
                loginController.getOldConfigs().setOpacity(1);
                loginController.getOldConfigs().setDisable(false);
            }
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getIpID().setOpacity(1);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getIpID().setDisable(false);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setOpacity(1);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setDisable(false);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setOpacity(1);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setDisable(false);
            LoginController.requestIP();
            while (chosenConfig.size()<2) {
                while (!inputReceived)
                    unlockCondition.await();
                inputReceived=false;
                chosenConfig.add(inputString);
                if(chosenConfig.size()== 1)
                    LoginController.requestUsername();
            }
        }catch (InterruptedException e) {
            printLogo();
            return askToReloadLastSettings(savedUsers);
        } finally {
            lock.unlock();
        }
        return chosenConfig;
    }

    @Override
    public boolean chooseMatchReload() {
        return false;
    }

    @Override
    public void printLogo() {
        try {
            Parent panel;
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("LoginResources/santorini.fxml"));
            panel = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.setGUI(this);
            scene.setUserData(fxmlLoader);
            currentView = ViewsType.LOGIN;
            scene.setRoot(panel);
        } catch (IOException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String askIP() {
        inputReceived = false;
        lock.lock();
        try {
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getIpID().setOpacity(1);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getIpID().setDisable(false);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setOpacity(1);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getUsernameID().setDisable(false);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setOpacity(1);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setDisable(false);
            LoginController.requestIP();
            while (!inputReceived)
                unlockCondition.await();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        String tmp = inputString;
        inputString = "";
        return tmp;
    }

    @Override
    public String askUsername() {
        inputReceived=false;
        lock.lock();
        try {
            if(currentView!=ViewsType.LOGIN) {
                printLogo();
                LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
                loginController.setIpIsSet(true);
            }
            LoginController loginController = ((FXMLLoader) scene.getUserData()).getController();
            loginController.getUsernameID().setOpacity(1);
            loginController.getUsernameID().setDisable(false);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setOpacity(1);
            ((LoginController)((FXMLLoader) scene.getUserData()).getController()).getLoginBtn().setDisable(false);
            LoginController.requestUsername();
            while (!inputReceived)
                unlockCondition.await();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Platform.exit();
            System.exit(0);
        } finally {
            lock.unlock();
        }
        String tmp = inputString;
        inputString = "";
        return tmp;
    }

    @Override
    public void gameStartScreen(List<Cell> gameBoard) {

    }

    @Override
    public String lobbyOptions(List<String> options) {
        lock.lock();
        inputReceived=false;
        try {
            Parent panel;
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("LoginResources/lobbyView.fxml"));
            panel = fxmlLoader.load();
            LobbyController lobbyController = fxmlLoader.getController();
            lobbyController.setGUI(this);
            if(options.size()==1){
                lobbyController.getJoinBtn().setDisable(true);
            }
            scene.setUserData(fxmlLoader);
            currentView = ViewsType.LOBBY;
            scene.setRoot(panel);
        } catch (IOException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        try {
            LobbyController.setInputRequested(true);
            while (!inputReceived)
                unlockCondition.await();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        String tmp = inputString;
        inputString = "";
        return tmp;
    }

    @Override
    public String askLobbyName() {
        return "null";
    }

    @Override
    public int askLobbySize() {
        return 2;
    }

    @Override
    public String chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) {
        currentView = ViewsType.LOBBY;
        setRoot("LoginResources/chooseLobby");
        LobbyController controller =((FXMLLoader) scene.getUserData()).getController();
        controller.lobbyList.getItems().addAll(lobbiesAvailable.keySet());
        controller.setGUI(this);
        inputReceived=false;
        lock.lock();
        try {
            LobbyController.setInputRequested(true);
            while (!inputReceived)
                unlockCondition.await();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        String tmp = inputString;
        inputString = "";
        return tmp;

    }

    @Override
    public Cell chooseWorker() {
        return null;
    }

    @Override
    public Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        return null;
    }

    @Override
    public Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {
        return null;
    }

    @Override
    public Block chooseBlockToBuild(List<Block> buildableBlocks) {
        return null;
    }

    @Override
    public GodData chooseUserGod(List<GodData> possibleGods) {
        return null;
    }

    @Override
    public List<GodData> chooseGameGods(List<GodData> allGods, int size) {
        inputReceived = false;
        setRoot("chooseGods");
        lock.lock();
        List<GodData> chosenGods = new ArrayList<>();
        ChooseGodsController controller = ((FXMLLoader) scene.getUserData()).getController();
        controller.setGUI(this);
        try {
            for (int i = 0; i < size; i++) {
                controller.setInputRequested(true);
                while (!inputReceived)
                    unlockCondition.await();
                chosenGods.add(allGods.stream().filter(godData -> godData.getName().equals(inputString)).collect(Collectors.toList()).get(0));
                inputReceived=false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return chosenGods;
    }

    @Override
    public String chooseStartingPlayer(List<String> players) {
        return null;
    }

    @Override
    public Cell placeWorker() {
        return null;
    }

    @Override
    public PossibleActions chooseAction(List<PossibleActions> possibleActions) {
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

 */
}
