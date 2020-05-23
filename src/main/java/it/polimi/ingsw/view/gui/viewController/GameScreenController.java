package it.polimi.ingsw.view.gui.viewController;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.view.gui.GUI;
import it.polimi.ingsw.view.gui.utils.MapTileImage;
import javafx.fxml.FXML;
import javafx.scene.effect.Glow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GameScreenController {

    @FXML
    private GridPane mapGrid;
    private GUI gui;
    private List<MapTileImage> mapTiles;

    public GridPane getMapGrid() {
        return mapGrid;

    }

    public void setGui(GUI gui) {
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
