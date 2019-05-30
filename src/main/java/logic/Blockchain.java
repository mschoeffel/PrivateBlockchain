package logic;

import models.Block;
import models.Chain;
import models.Transaction;
import org.apache.log4j.Logger;
import sun.security.provider.SHA;
import utils.SHA3Util;
import utils.VerificationUtil;

import java.math.BigInteger;
import java.util.*;
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
        if(VerificationUtil.verifyBlock(block)){
            byte[] previousBlockHash = block.getBlockHeader().getPreviousHash();

            if(previousBlockIsBestBlock(previousBlockHash)){
                block.setBlockNumber(chain.size());
                chain.add(block);
                bestBlock = block;
                DependencyManager.getPendingTransactions().clearPendingTransactions(block);
            } else{
                checkAltChains(previousBlockHash, block);
            }
        }
    }

    private void checkAltChains(byte[] previousBlockHash, Block block){
        boolean isNoBlockOfAltChain = true;
        for(Chain altChain : altChains){
            if(Arrays.equals(altChain.getLast().getBlockHash(), previousBlockHash)){
                block.setBlockNumber(altChain.size());
                altChain.add(block);
                switchChainsIfNecessary(altChain);
                isNoBlockOfAltChain = false;
                break;
            }
        }
        if(isNoBlockOfAltChain){
            createNewAltChain(previousBlockHash, block);
        }
    }

    private void createNewAltChain(byte[] previousBlockHash, Block block){
        Chain chain = getChainForBlock(getBlockByHash(previousBlockHash));

        for(int i = chain.getChain().size() -1; i >= 0; i--){
            if(Arrays.equals(chain.get(i).getBlockHash(), previousBlockHash)){
                List<Block> newChain = new CopyOnWriteArrayList<>(chain.getChain().subList(0,i+1));
                block.setBlockNumber(newChain.size());
                newChain.add(block);
                Chain newChainChain = new Chain(NETWORK_ID, newChain);
                altChains.add(newChainChain);
                i = -1;

                switchChainsIfNecessary(newChainChain);
            }
        }
    }

    private void switchChainsIfNecessary(Chain chain){
        if(chain.size() > this.chain.size()){
            correctPendingTransactions(this.chain, chain);
            this.chain = chain;
            this.bestBlock = chain.getLast();
        }
    }

    private void correctPendingTransactions(Chain previousChain, Chain chain){
        int index = getIndexOfFork(previousChain, chain);

        Set<Transaction> transactionsToRemove = new HashSet<>();
        for(int i = index; i < chain.size(); i++){
            transactionsToRemove.addAll(chain.get(i).getTransactions());
        }

        Set<Transaction> transactionsToInsert = new HashSet<>();
        for(int i = index; i < previousChain.size(); i++){
            previousChain.get(i).getTransactions().forEach(item -> {
                if(!transactionsToRemove.contains(item)){
                    transactionsToInsert.add(item);
                }
            });
        }

        DependencyManager.getPendingTransactions().clearPendingTransactions(transactionsToRemove);
        DependencyManager.getPendingTransactions().addPendingTransactions(transactionsToInsert);
    }

    private int getIndexOfFork(Chain previousChain, Chain chain){
        int index = -1;
        for(int i = previousChain.size()-1; i >= 0; i--){
            index = chain.getChain().indexOf(previousChain.get(i));
            if(index > -1){
                break;
            }
        }
        return (index > -1) ? (index + 1) : 0;
    }

    private boolean previousBlockIsBestBlock(byte[] blockHash){
        return Arrays.equals(DependencyManager.getBlockchain().getPreviousHash(), blockHash);
    }

    private Chain getChainForBlock(Block block){
        Chain result = null;

        int index = chain.getChain().indexOf(block);

        if(index == -1){
            for (Chain altChain : altChains) {
                if(altChain.getChain().indexOf(block) > -1){
                    result = altChain;
                }
            }
        } else{
            result = chain;
        }
        return result;
    }

    public Block getGenesisBlock(){
        return chain.get(0);
    }
    public byte[] getPreviousHash(){ return bestBlock.getBlockHash();}
    public Block getBlockByHash(byte[] hash){
        return blockCache.get(SHA3Util.digestToHex(hash));
    }

}
