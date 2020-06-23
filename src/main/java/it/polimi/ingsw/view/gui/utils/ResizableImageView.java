package it.polimi.ingsw.view.gui.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ResizableImageView extends ImageView {

    //TODO: move this in a dedicated class
    private int index = -1;
    private String name;


    public ResizableImageView(Image image) {
        super(image);
        setPreserveRatio(false);
    }

    public ResizableImageView() {
        setPreserveRatio(false);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double minWidth(double height) {
        return 40;
    }

    @Override
    public double maxWidth(double height) {
        return 16384;
    }

    @Override
    public double minHeight(double width) {
        return 40;
    }

    @Override
    public double maxHeight(double width) {
        return 16384;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        setFitWidth(width);
        setFitHeight(height);
    }

}
