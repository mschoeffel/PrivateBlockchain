package logic;

import models.Block;
import models.Chain;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.SHA3Util;
import utils.VerificationUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class represents the blockchain itself
 */
public class Blockchain {

    //Logger to display additional information
    private static Logger logger = Logger.getLogger(Blockchain.class);

    //Max size of a single block in byte
    public final static int MAX_BLOCK_SIZE_BYTES = 1048576;
    //Version number of the blockchain
    public final static int VERSION = 1;
    //Network Id of the blockchain
    public final static int NETWORK_ID = 1;
    //Amount of coins given as reward for mining a new valid block that made it to the chain
    public final static double BLOCK_REWARD = 50.0;
    //Number of verifications needed to take a block as valid
    public final static int REQUIRED_BLOCK_CONFIRMATIONS = 1;
    //Amount how often the fee base price gets added to a transaction
    public final static int TRANSACTION_FEE_UNITS = 10;

    //Difficulty of the blockchain
    private BigInteger difficulty;
    //List of alternative chains that are existing
    private List<Chain> altChains;
    //Best/Most recent, verified block
    private Block bestBlock;
    //The chain of the blockchain
    private Chain chain;
    //Cache of blocks
    private Map<String, Block> blockCache;
    //Cache of transactions
    private Map<String, Transaction> transactionCache;

    /**
     * Creates a new empty blockchain
     */
    public Blockchain() {
        logger.info("Blockchain: Blockchain created.");
        this.altChains = new CopyOnWriteArrayList<>();
        this.chain = new Chain(NETWORK_ID);
        this.altChains.add(chain);
        this.bestBlock = this.chain.getLast();
        this.blockCache = new ConcurrentHashMap<>();
        this.blockCache.put(SHA3Util.digestToHex(getGenesisBlock().getBlockHash()), getGenesisBlock());
        this.transactionCache = new ConcurrentHashMap<>();
        this.difficulty = new BigInteger("-57896000000000000000000000000000000000000000000000000000000000000000000000000");
    }

    /**
     * Creates a new blockchain with given difficulty and alternative chains
     *
     * @param difficulty Difficulty
     * @param altChains  Alternative chains
     */
    public Blockchain(BigInteger difficulty, List<Chain> altChains) {
        logger.info("Blockchain: Blockchain created.");
        this.difficulty = difficulty;
        this.altChains = altChains;
        this.blockCache = new ConcurrentHashMap<>();
        this.transactionCache = new ConcurrentHashMap<>();

        int max = 0;
        Chain chain = null;

        for (Chain altChain : altChains) {
            if (max < altChain.size()) {
                max = altChain.size();
                chain = altChain;
            }

            for (Block block : altChain.getChain()) {
                this.blockCache.put(SHA3Util.digestToHex(block.getBlockHash()), block);

                for (Transaction transaction : block.getTransactions()) {
                    this.transactionCache.put(transaction.getTxIdAsString(), transaction);
                }
            }
        }

        this.chain = chain;
        this.bestBlock = chain.getLast();
    }

    /**
     * Verifies and adds a new block to the blockchain if the block is valid
     *
     * @param block Block to add
     */
    public synchronized void addBlock(Block block) {
        logger.info("Blockchain: New block added.");
        if (VerificationUtil.verifyBlock(block)) {
            byte[] previousBlockHash = block.getBlockHeader().getPreviousHash();

            if (previousBlockIsBestBlock(previousBlockHash)) {
                block.setBlockNumber(chain.size());
                chain.add(block);
                bestBlock = block;
                DependencyManager.getPendingTransactions().clearPendingTransactions(block);
            } else {
                checkAltChains(previousBlockHash, block);
            }

            blockCache.put(SHA3Util.digestToHex(block.getBlockHash()), block);

            block.getTransactions().forEach(transaction -> transactionCache.put(transaction.getTxIdAsString(), transaction));

            DependencyManager.getAccountStorage().parseBlock(block);
        }
    }

    /**
     * Checks if a block can be linked to a alternative chain and if not a new alternative chain is created
     *
     * @param previousBlockHash Previous hash of the block
     * @param block             Block
     */
    private void checkAltChains(byte[] previousBlockHash, Block block) {
        boolean isNoBlockOfAltChain = true;
        for (Chain altChain : altChains) {
            if (Arrays.equals(altChain.getLast().getBlockHash(), previousBlockHash)) {
                block.setBlockNumber(altChain.size());
                altChain.add(block);
                switchChainsIfNecessary(altChain);
                isNoBlockOfAltChain = false;
                break;
            }
        }
        if (isNoBlockOfAltChain) {
            createNewAltChain(previousBlockHash, block);
        }
    }

    /**
     * Creates a new alternative chain with a given block an previous hash
     *
     * @param previousBlockHash Previous hash
     * @param block             Block
     */
    private void createNewAltChain(byte[] previousBlockHash, Block block) {
        Chain chain = getChainForBlock(getBlockByHash(previousBlockHash));

        for (int i = chain.getChain().size() - 1; i >= 0; i--) {
            if (Arrays.equals(chain.get(i).getBlockHash(), previousBlockHash)) {
                List<Block> newChain = new CopyOnWriteArrayList<>(chain.getChain().subList(0, i + 1));
                block.setBlockNumber(newChain.size());
                newChain.add(block);
                Chain newChainChain = new Chain(NETWORK_ID, newChain);
                altChains.add(newChainChain);
                i = -1;

                switchChainsIfNecessary(newChainChain);
            }
        }
    }

    /**
     * Switches the main chain if the given chain is longer and valid
     *
     * @param chain Chain to check
     */
    private void switchChainsIfNecessary(Chain chain) {
        if (chain.size() > this.chain.size()) {
            logger.info("Blockchain: Chain switched.");
            correctPendingTransactions(this.chain, chain);
            this.chain = chain;
            this.bestBlock = chain.getLast();
        }
    }

    /**
     * Corrects the open pending transaction when switching the main chain
     *
     * @param previousChain Previous chain
     * @param chain         Chain to switch to
     */
    private void correctPendingTransactions(Chain previousChain, Chain chain) {
        int index = getIndexOfFork(previousChain, chain);

        Set<Transaction> transactionsToRemove = new HashSet<>();
        for (int i = index; i < chain.size(); i++) {
            transactionsToRemove.addAll(chain.get(i).getTransactions());
        }

        Set<Transaction> transactionsToInsert = new HashSet<>();
        for (int i = index; i < previousChain.size(); i++) {
            previousChain.get(i).getTransactions().forEach(item -> {
                if (!transactionsToRemove.contains(item)) {
                    transactionsToInsert.add(item);
                }
            });
        }

        DependencyManager.getPendingTransactions().clearPendingTransactions(transactionsToRemove);
        DependencyManager.getPendingTransactions().addPendingTransactions(transactionsToInsert);
    }

    /**
     * Returns the index of the block of the fork (Chain switch)
     *
     * @param previousChain Previous chain
     * @param chain         Chain switched to
     * @return Index of the last same block
     */
    private int getIndexOfFork(Chain previousChain, Chain chain) {
        int index = -1;
        for (int i = previousChain.size() - 1; i >= 0; i--) {
            index = chain.getChain().indexOf(previousChain.get(i));
            if (index > -1) {
                break;
            }
        }
        return (index > -1) ? (index + 1) : 0;
    }

    /**
     * Checks if the previous block is the best (most recent verified) block
     *
     * @param blockHash Blockhash to check from
     * @return Boolean if the previous block to the given hash is the best block
     */
    private boolean previousBlockIsBestBlock(byte[] blockHash) {
        return Arrays.equals(DependencyManager.getBlockchain().getPreviousHash(), blockHash);
    }

    /**
     * Returns the chain to a given block
     *
     * @param block Block to get the chain from
     * @return Chain to the given block
     */
    private Chain getChainForBlock(Block block) {
        Chain result = null;

        int index = chain.getChain().indexOf(block);

        if (index == -1) {
            for (Chain altChain : altChains) {
                if (altChain.getChain().indexOf(block) > -1) {
                    result = altChain;
                }
            }
        } else {
            result = chain;
        }
        return result;
    }

    /**
     * Checks if a digit fulfills the set difficulty
     *
     * @param digest Digest to check
     * @return Boolean if the given digest fulfills the set difficulty
     */
    public boolean fulfillsDifficulty(byte[] digest) {
        BigInteger temp = new BigInteger(digest);
        return temp.compareTo(difficulty) <= 0;
    }

    /**
     * Gets the latest block given to a specific offset and size
     *
     * @param size   Amount of blocks you want to get as List
     * @param offset Offset from the start of the chain
     * @return List of blocks
     */
    public List<Block> getLatestBlocks(int size, int offset) {
        List<Block> blocks = new ArrayList<>();
        Block block = this.getLatestBlock();

        for (int i = 0; i < (size + offset); i++) {
            if (block != null) {
                if (i >= offset) {
                    blocks.add(block);
                }
                String previousHash = SHA3Util.digestToHex(block.getBlockHeader().getPreviousHash());
                block = this.getBlockByHash(previousHash);
            }
        }
        return blocks;
    }

    /**
     * Returns the next block in the chain to a given block
     *
     * @param block Block to get the next/children from
     * @return Child/next block to the given one
     */
    public Block getChildOfBlock(Block block) {
        Block result = null;
        Chain chain = getChainForBlock(block);

        if (chain != null) {
            result = chain.get(chain.getChain().indexOf(block) + 1);
        }
        return result;
    }

    //Getter Setter:

    public Block getGenesisBlock() {
        return chain.get(0);
    }

    public byte[] getPreviousHash() {
        return bestBlock.getBlockHash();
    }

    public Block getBlockByHash(byte[] hash) {
        return blockCache.get(SHA3Util.digestToHex(hash));
    }

    public Block getBlockByHash(String hash) {
        return blockCache.get(hash);
    }

    public Block getLatestBlock() {
        return bestBlock;
    }

    public Transaction getTransactionByHash(String hash) {
        return transactionCache.get(hash);
    }

    public BigInteger getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(BigInteger difficulty) {
        this.difficulty = difficulty;
    }

    public List<Chain> getAltChains() {
        return altChains;
    }

    public void setAltChains(List<Chain> altChains) {
        this.altChains = altChains;
    }

    public Block getBestBlock() {
        return bestBlock;
    }

    public void setBestBlock(Block bestBlock) {
        this.bestBlock = bestBlock;
    }

    public Chain getChain() {
        return chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }

    public Map<String, Block> getBlockCache() {
        return blockCache;
    }

    public void setBlockCache(Map<String, Block> blockCache) {
        this.blockCache = blockCache;
    }

    public Map<String, Transaction> getTransactionCache() {
        return transactionCache;
    }

    public void setTransactionCache(Map<String, Transaction> transactionCache) {
        this.transactionCache = transactionCache;
    }

    public int size() {
        return chain.size();
    }
}
