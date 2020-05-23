package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.gui.viewController.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
@Deprecated
public class GuiInit extends Application {

    private static Scene scene;
    private static LoginController loginController;

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiInit.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static LoginController getLoginController() {
        return loginController;
    }

    @Override
    public void start(Stage primarystage) throws IOException {
        Parent panel;
        FXMLLoader fxmlLoader = new FXMLLoader(GuiInit.class.getResource("LoginResources/santorini.fxml"));
        panel = fxmlLoader.load();
        loginController = fxmlLoader.getController();

        scene = new Scene(panel);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }

}