package logic;

import models.Block;
import models.Chain;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.SHA3Util;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Blockchain {

    private static Logger logger = Logger.getLogger(Blockchain.class);

    public final static int MAX_BLOCK_SIZE_BYTES = 1048576;
    public final static int VERSION = 1;
    public final static int NETWORK_ID = 1;
    public final static double BLOCK_REWARD = 50.0;
    public final static int REQUIRED_BLOCK_CONFIRMATIONS = 1;
    public final static int TRANSACTION_FEE_UNITS = 10;

    private BigInteger difficulty;
    private List<Chain> altChains;
    private Block bestBlock;
    private Chain chain;
    private Map<String, Block> blockCache;
    private Map<String, Transaction> transactionCache;

    public Blockchain (){

        this.altChains = new CopyOnWriteArrayList<>();
        this.chain = new Chain(NETWORK_ID);
        this.altChains.add(chain);
        this.bestBlock = this.chain.getLast();
        this.blockCache = new ConcurrentHashMap<>();
        this.blockCache.put(SHA3Util.digestToHex(getGenesisBlock().getBlockHash()), getGenesisBlock());
        this.transactionCache = new ConcurrentHashMap<>();
        this.difficulty = new BigInteger("-57896000000000000000000000000000000000000000000000000000000000000000000000000");
    }

    public Blockchain (BigInteger difficulty, List<Chain> altChains){
        this.difficulty = difficulty;
        this.altChains = altChains;
        this.blockCache = new ConcurrentHashMap<>();
        this.transactionCache = new ConcurrentHashMap<>();

        int max = 0;
        Chain chain = null;

        for(Chain altChain: altChains){
            if(max < altChain.size()){
                max = altChain.size();
                chain = altChain;
            }

            for(Block block : altChain.getChain()){
                this.blockCache.put(SHA3Util.digestToHex(block.getBlockHash()), block);

                for(Transaction transaction : block.getTransactions()){
                    this.transactionCache.put(transaction.getTxIdAsString(), transaction);
                }
            }
        }

        this.chain = chain;
        this.bestBlock = chain.getLast();
    }

    public synchronized  void addBlock(Block block){
        //
    }

    public Block getGenesisBlock(){
        return chain.get(0);
    }

}
