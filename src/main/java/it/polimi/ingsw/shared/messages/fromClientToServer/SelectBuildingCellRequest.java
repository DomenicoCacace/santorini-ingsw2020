package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.server.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

public class SelectBuildingCellRequest extends MessageFromClientToServer {

    private final Cell selectedCell;

    @JsonCreator
    public SelectBuildingCellRequest(@JsonProperty("username") String username, @JsonProperty("selected cell") Cell selectedCell) {
        super(username, Type.CLIENT_REQUEST);
        this.selectedCell = selectedCell;
    }

    public Cell getSelectedCell() {
        return selectedCell;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.selectCellToBuild(this);
    }

}
