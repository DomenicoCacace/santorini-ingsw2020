package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

import java.util.List;

public class SelectBuildingCellResponse extends MessageFromServerToClient {

    private final List<Block> blocks;

    @JsonCreator
    public SelectBuildingCellResponse(@JsonProperty("username") String username, @JsonProperty("blocks") List<Block> blocks) {
        super(username, Content.SELECT_BUILDING_CELL);
        this.blocks = blocks;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onBuildingCellSelected(this);
    }
}
