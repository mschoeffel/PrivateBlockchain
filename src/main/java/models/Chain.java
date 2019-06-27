package models;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that represents the chain of a blockchain
 */
public class Chain {

    //Logger to display additional information
    private Logger logger = Logger.getLogger(Chain.class);
    //List of blocks of the chain
    private List<Block> chain = new CopyOnWriteArrayList<>();
    //Network Id of the blockchain network
    private int networkId;

    /**
     * Creates a new empty chain
     */
    public Chain() {
        logger.info("Chain: Chain created.");
    }

    /**
     * creates a new chain with a network Id and an already existing list ob blocks
     *
     * @param networkId Network Id of the blockchain network
     * @param chain     Chain of blocks
     */
    public Chain(int networkId, List<Block> chain) {
        this();
        this.networkId = networkId;
        this.chain = chain;
    }

    /**
     * Creates a new chain with a network Id
     *
     * @param networkId Network Id of the blockchain network
     */
    public Chain(int networkId) {
        this();
        this.networkId = networkId;
        chain.add(new GenesisBlock());
    }

    /**
     * Adds a block to the chain
     *
     * @param block Block to add
     */
    public void add(Block block) {
        logger.info("Chain: Block added.");
        chain.add(block);
    }

    /**
     * Returns a block of the chain
     *
     * @param index Index of the block in the chain
     * @return Block or null if the index doesnt exist
     */
    public Block get(int index) {
        if (index < chain.size()) {
            return chain.get(index);
        } else {
            return null;
        }
    }

    /**
     * Returns the last block of the chain
     *
     * @return Last block of the chain
     */
    public Block getLast() {
        return chain.get((chain.size() == 0) ? 0 : (chain.size() - 1));
    }

    /**
     * Returns the size of the chain
     *
     * @return Size of the chain
     */
    public int size() {
        return chain.size();
    }

    //Getter Setter:

    public List<Block> getChain() {
        return chain;
    }

    public void setChain(List<Block> chain) {
        logger.info("Chain: New chain set.");
        this.chain = chain;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    //Serialization:

    /**
     * Returns a JSON representation of the object
     *
     * @return JSON String
     */
    @Override
    public String toString() {
        return "Chain{ chain=" + chain + ", networkId=" + networkId + "}";
    }
}
