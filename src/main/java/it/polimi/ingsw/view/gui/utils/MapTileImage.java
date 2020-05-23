package it.polimi.ingsw.view.gui.utils;
import it.polimi.ingsw.model.Color;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

public class MapTileImage extends ResizableImageView {

    //TODO: Rename to PNG!!
    public static final String BLOCK1_DIR = "block1.jpg";
    public static final String BLOCK2_DIR = "block2.jpg";
    public static final String BLOCK3_DIR = "block3.jpg";
    public static final String DOME_DIR = "dome.png";
    public static final String BLUE_WORKER_DIR = "blueWorker.jpg";
    public static final String RED_WORKER_DIR = "redWorker.png";
    public static final String PURPLE_WORKER_DIR = "purpleWorker.png";

    private int height;
    private boolean isOccupied;
    private boolean isCellTile;
    private MapTileImage printedWorker;

    public MapTileImage() {
        super();
    }

    public void setCellTile(boolean cellTile) {
        isCellTile = cellTile;
    }

    public MapTileImage(Image image) {
        super(image);
    }

    public boolean isCellTile() {
        return isCellTile;
    }

    public int getHeight() {
        return height;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void printBlock(int height){
        StackPane stackPane = ((StackPane) this.getParent());
        MapTileImage mapTileImage = new MapTileImage(chooseImage(height));
        mapTileImage.isCellTile=false;
        mapTileImage.setFitHeight(this.getFitHeight() - 0.5);
        mapTileImage.setFitWidth(this.getFitWidth() - 0.5);
        this.height = height;
        stackPane.getChildren().add(mapTileImage);
    }

    private Image chooseImage(int height){
        switch (height){
            case 1:
                return new Image(this.getClass().getResourceAsStream(BLOCK1_DIR));
            case 2:
                return new Image(this.getClass().getResourceAsStream(BLOCK2_DIR));
            case 3:
                return new Image(this.getClass().getResourceAsStream(BLOCK3_DIR));
            case 4:
                return new Image(this.getClass().getResourceAsStream(DOME_DIR));
            default: return null; //Shouldn't be here
        }
    }

    public void printBuilding(int height){
        for(int i = 1; i <= height; i++){
            printBlock(i);
        }
    }

    private Image chooseWorker(Color color){
        switch (color){
            case BLUE:
                return new Image(this.getClass().getResourceAsStream(BLUE_WORKER_DIR));
            case RED:
                return new Image(this.getClass().getResourceAsStream(RED_WORKER_DIR));
            case PURPLE:
                return new Image(this.getClass().getResourceAsStream(PURPLE_WORKER_DIR));
            default: return null; //Shouldn't be here
        }
    }

    public void printWorker(Color color){
        if(isOccupied)
            removeWorker();
        else{
            StackPane stackPane = ((StackPane) this.getParent());
            printedWorker = new MapTileImage(chooseWorker(color));
            printedWorker.isCellTile=false;
            printedWorker.setFitHeight(this.getFitHeight() - 0.5);
            printedWorker.setFitWidth(this.getFitWidth() - 0.5);
            stackPane.getChildren().add(printedWorker);
            isOccupied = true;
        }
    }
    
    public void removeWorker(){
        StackPane stackPane = ((StackPane) this.getParent());
        stackPane.getChildren().removeAll(printedWorker);
    }
}
