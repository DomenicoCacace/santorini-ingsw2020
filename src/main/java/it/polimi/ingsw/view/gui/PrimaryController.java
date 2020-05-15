package it.polimi.ingsw.view.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PrimaryController {

    @FXML
    private GridPane mapGrid;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Pane pane;

    public Pane getPane() {
        return pane;
    }

    @FXML
    private void onCellSelected(MouseEvent event) throws FileNotFoundException {
        System.out.println(mapGrid);
        Node source = (Node) event.getSource();
        System.out.println(GridPane.getRowIndex(source.getParent()) + ", " + GridPane.getColumnIndex(source.getParent()));
        Image image = new Image(new FileInputStream("/home/alba/Desktop/Uni/Ing Soft/Progetto/javaFX/src/main/resources/org/example/gui/trophy_large.png"));
        ResizableImageView resizableImageView = new ResizableImageView(image);
        mapGrid.add(resizableImageView,GridPane.getColumnIndex(source.getParent()), GridPane.getRowIndex(source.getParent()));
    }

    @FXML
    private void switchToSecondary(MouseEvent event) {
        System.out.println(mapGrid);
        Node source = (Node) event.getSource();
        System.out.println(GridPane.getRowIndex(source) + ", " + GridPane.getColumnIndex(source));
        //App.setRoot("secondary");
    }

    public GridPane getMapGrid() {
        return mapGrid;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }
}
