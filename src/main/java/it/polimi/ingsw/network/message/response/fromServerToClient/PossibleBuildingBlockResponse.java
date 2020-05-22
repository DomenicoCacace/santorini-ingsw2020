package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class PossibleBuildingBlockResponse extends MessageFromServerToClient {

    private final List<Block> blocks;

    @JsonCreator
    public PossibleBuildingBlockResponse(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("blocks") List<Block> blocks) {
        super(username, type);
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
