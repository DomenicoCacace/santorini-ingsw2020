package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.view.gui.GUI;
import it.polimi.ingsw.view.gui.utils.MapTileImage;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Glow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GameScreenController {

    @FXML
    private SplitPane splitPane;
    @FXML
    private ListView<String> choiceList;
    @FXML
    private GridPane mapGrid;
    @FXML
    private TextArea textArea;
    private GUI gui;
    private List<MapTileImage> mapTiles;

    public GridPane getMapGrid() {
        return mapGrid;
    }

    public ListView<String> getChoiceList() {
        return choiceList;
    }

    public void setGui(GUI gui) {
        splitPane.widthProperty().addListener((observableValue, oldValue, newValue) ->{
            splitPane.setDividerPositions(0.2, 0.8);
            splitPane.maxHeightProperty().setValue(splitPane.widthProperty().getValue() * 9/16);
            splitPane.minHeightProperty().setValue(splitPane.widthProperty().getValue() * 9/16);
        } );
        this.gui = gui;
        mapTiles = new ArrayList<>();
        mapGrid.getChildren().forEach(node -> {
            ((StackPane) node).getChildren().forEach(mapTile -> {
                if(((MapTileImage) mapTile).isCellTile()) {
                    mapTiles.add(((MapTileImage) mapTile));
                    ((MapTileImage) mapTile).setController(this);
                }
            });
        });
    }

    public void onOptionChosen(){
        if(choiceList.getSelectionModel().getSelectedItem()!=null) {
            choiceList.setDisable(true);
            gui.setInputString(String.valueOf(choiceList.getSelectionModel().getSelectedIndex() + 1));
            choiceList.getItems().clear();
        }
    }

    public void handleCellCLicked(int row, int col){
        mapTiles.forEach(mapTileImage -> {
            mapTileImage.setDisable(true);
            mapTileImage.setEffect(null);
        });
        gui.setInputString(String.valueOf(row));
        gui.setInputString(String.valueOf(col));
    }

    public void makeCellsClickable(List<Cell> availableCell){
        availableCell.forEach(cell -> {
            ListIterator<MapTileImage> mapTileImageListIterator = mapTiles.listIterator();
            MapTileImage currentMapTile;
            while(mapTileImageListIterator.hasNext()){
                currentMapTile = mapTileImageListIterator.next();
                if(GridPane.getRowIndex(currentMapTile.getParent()).equals(cell.getCoordX() +1 ) &&
                        GridPane.getColumnIndex(currentMapTile.getParent()).equals(cell.getCoordY() + 1)){
                    currentMapTile.setDisable(false);
                    currentMapTile.setEffect(new Glow(1));
                    break;
                }
                else if(!mapTileImageListIterator.hasNext())
                    currentMapTile.setDisable(true);
            }
        });
    }

    public void allCellsClickable(){
        mapTiles.forEach(mapTileImage -> mapTileImage.setDisable(false));
    }
}
