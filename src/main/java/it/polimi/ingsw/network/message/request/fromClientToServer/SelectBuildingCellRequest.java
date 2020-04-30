package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;

public class SelectBuildingCellRequest extends MessageFromClientToServer {

    private final Cell selectedCell;

    @JsonCreator
    public SelectBuildingCellRequest(@JsonProperty("username") String username, @JsonProperty("selected cell") Cell selectedCell) {
        super(username, Content.SELECT_BUILDING_CELL);
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
