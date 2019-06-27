package network.adapters;

import com.owlike.genson.annotation.JsonIgnore;
import models.Block;
import models.Chain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Chain adapter to wrap a chain
 */
public class ChainAdapter {

    //Chain to wrap
    private Chain chain;

    /**
     * Creates a new chain adapter with new chain
     */
    public ChainAdapter() {
        this.chain = new Chain();
    }

    /**
     * Creates new chain adapter with given chain
     *
     * @param chain Given chain to wrap
     */
    public ChainAdapter(Chain chain) {
        this.chain = chain;
    }

    //Wrapping methods:

    @JsonIgnore
    public Chain getChainObject() {
        return chain;
    }

    public List<BlockAdapter> getChain() {
        List<BlockAdapter> blocks = new ArrayList<>();

        for (Block block : chain.getChain()) {
            blocks.add(new BlockAdapter(block));
        }

        return blocks;
    }

    public void setChain(List<BlockAdapter> chain) {
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<>();

        for (BlockAdapter blockAdapter : chain) {
            blocks.add(blockAdapter.getBlock());
        }

        this.chain.setChain(blocks);
    }

    public int getNetworkId() {
        return chain.getNetworkId();
    }

    public void setNetworkId(int networkId) {
        this.chain.setNetworkId(networkId);
    }
}
