package network.adapters;

import com.owlike.genson.annotation.JsonIgnore;
import logic.Blockchain;
import models.Chain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Blockchain adapter to wrap a blockchain
 */
public class BlockchainAdapter {

    //blockchain to warp
    private Blockchain blockchain;

    //Difficulty of the blockchain
    private BigInteger difficulty;

    //Alternative chains
    private List<Chain> altChains;

    /**
     * Creates new empty blockchain adapter
     */
    public BlockchainAdapter() {
    }

    /**
     * Creates new blockchain adapter with given blockchain
     *
     * @param blockchain Given blockchain to wrap
     */
    public BlockchainAdapter(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    //Wrappering methods:

    @JsonIgnore
    public Blockchain getBlockchain() {
        if (blockchain == null) {
            blockchain = new Blockchain(difficulty, altChains);
        }

        return blockchain;
    }

    public String getDifficulty() {
        return (this.blockchain.getDifficulty() == null) ?
                difficulty.toString() :
                blockchain.getDifficulty().toString();
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = new BigInteger(difficulty);
    }

    public List<ChainAdapter> getAltChains() {
        List<Chain> altChains = (this.blockchain.getAltChains() == null) ? this.altChains : blockchain.getAltChains();

        List<ChainAdapter> chains = new ArrayList<>();

        for (Chain altChain : altChains) {
            chains.add(new ChainAdapter(altChain));
        }

        return chains;
    }

    public void setAltChains(List<ChainAdapter> altChains) {
        List<Chain> chains = new CopyOnWriteArrayList<>();

        for (ChainAdapter altChain : altChains) {
            chains.add(altChain.getChainObject());
        }

        this.altChains = chains;
    }
}
