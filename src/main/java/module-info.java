open module AM37 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires java.logging;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.view.gui to javafx.graphics;
    exports it.polimi.ingsw.view.gui.utils to javafx.graphics;
    exports it.polimi.ingsw.model to com.fasterxml.jackson.databind;
}