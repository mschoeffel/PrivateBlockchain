package models;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chain {
    private Logger logger = Logger.getLogger(Chain.class);
    private List<Block> chain = new CopyOnWriteArrayList<>();
    private int networkId;

    public Chain(){
        logger.info("Chain: Chain created.");
    }

    public Chain(int networkId, List<Block> chain){
        super();
        this.networkId = networkId;
        this.chain = chain;
    }

    public Chain(int networkId){
        super();
        this.networkId = networkId;
        chain.add(new GenesisBlock());
    }

    public void add(Block block){
        logger.info("Chain: Block added.");
        chain.add(block);
    }

    public Block get(int index){
        if(index < chain.size()) {
            return chain.get(index);
        } else{
            return null;
        }
    }

    public Block getLast(){
        return chain.get((chain.size() == 0) ? 0 : (chain.size() - 1));
    }

    public int size(){
        return chain.size();
    }

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

    @Override public String toString(){
        return "Chain{ chain=" + chain + ", networkId=" + networkId + "}";
    }
}
