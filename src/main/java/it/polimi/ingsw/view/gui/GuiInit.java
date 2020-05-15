package it.polimi.ingsw.view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class GuiInit extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primarystage) throws IOException {
        Parent panel;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("allMap.fxml"));
        panel = fxmlLoader.load();
        PrimaryController controller = fxmlLoader.getController();
        System.out.println(controller.getMapGrid());
/*
        controller.getMapGrid().setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("/home/alba/Desktop/Uni/Ing Soft/Progetto/javaFX/src/main/resources/org/example/gui/SmallMap.png")),
                null, null, null, null)));
 */
        controller.getSplitPane().widthProperty().addListener((observableValue, number, t1) -> {
            controller.getSplitPane().getItems().forEach(node -> {
                if(node.getClass().equals(AnchorPane.class)) {
                    node.resize(((AnchorPane) node).getWidth() * (t1.doubleValue()/number.doubleValue()), ((AnchorPane) node).getHeight());
                    ((AnchorPane) node).setMaxWidth(t1.doubleValue() * 0.20);
                }
            });
        });


        scene = new Scene(panel);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

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

}