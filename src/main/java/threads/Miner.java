package threads;

import logic.Blockchain;
import logic.DependencyManager;
import logic.PendingTransactions;
import models.Block;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.SignatureUtil;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Miner implements Runnable {

    //Logger to show some additional information
    private final static Logger logger = Logger.getLogger( Miner.class );
    //All the listener that listen to a miner
    private List<MinerListener> listeners = new ArrayList<>();
    //Boolean if the miner is running at the moment
    private boolean mining = true;
    //Boolean if the block the miner is mining at the moment should be canceled
    private boolean cancelBlock = false;
    //Actual mined block of the miner
    private Block block;
    //Unique ID of the miner
    private UUID minerId;
    //Keypair of the miner to store the received coins if a valid block is created
    private KeyPair keyPair;

    /**
     * Creates a new miner
     */
    public Miner() {
        minerId = UUID.randomUUID();
        keyPair = SignatureUtil.generateKeyPair();
        SignatureUtil.saveKeyPair(keyPair, minerId.toString());
    }

    /**
     * Starts the miner
     */
    @Override
    public void run() {
        logger.info("Miner: Miner started.");
        while (isMining()) {
            block = getNewBlockFromMining();

            while (!cancelBlock && doesNotFullfillDifficulty(block.getBlockHash())) {
                try {
                    block.incrementNonce();
                } catch (ArithmeticException e) {
                    restartMining();
                }
            }

            if (cancelBlock) {
                block = null;
                cancelBlock = false;
            } else {
                blockMined(block);
            }
        }
    }

    /**
     * Returns the new mined block
     * @return Block
     */
    private Block getNewBlockFromMining() {
        logger.info("Miner: Creating new Block.");

        PendingTransactions pendingTransactions = DependencyManager.getPendingTransactions();
        Blockchain blockchain = DependencyManager.getBlockchain();
        List<Transaction> transactions = pendingTransactions.getTransactionsForNextBlock();

        return new Block(transactions, blockchain.getPreviousHash());
    }

    /**
     * Checks if a hash fulfills the difficultly given by the network
     * @param digest hash of the block
     * @return Boolean if the hash fulfills the difficulty or not
     */
    private boolean doesNotFullfillDifficulty(byte[] digest) {
        Blockchain blockchain = DependencyManager.getBlockchain();
        return !blockchain.fulfillsDifficulty(digest);
    }

    /**
     * Restarts the miner with the new pending transactions
     */
    private void restartMining() {
        logger.info("Miner: Mining restarted.");

        PendingTransactions pendingTransactions = DependencyManager.getPendingTransactions();
        List<Transaction> transactions = pendingTransactions.getTransactionsForNextBlock();

        block.setTransactions(transactions);
    }

    /**
     * Miner mined a new block notify listeners and transactions and set keypair
     * @param block Block mined
     */
    private void blockMined(Block block) {

        logger.info("Miner: Block mined");

        block.setCoinbase(SignatureUtil.getCoinbaseFromPublicKey(keyPair));

        if (block.getTransactions().size() > 0) {
            block.getTransactions().forEach(transaction -> transaction.setBlockId(block.getBlockHash()));
        }

        DependencyManager.getPendingTransactions().clearPendingTransactions(block);
        DependencyManager.getBlockchain().addBlock(block);

        listeners.forEach(listener -> listener.notifyNewBlock(block));
    }

    /**
     * Returns if the miner is mining at the moment
     * @return Mining ath the moment  or not
     */
    public boolean isMining() {
        return mining;
    }

    /**
     * Stops the miner
     */
    public void stopMining() {
        logger.info("Miner: Miner stopped.");
        this.mining = false;
    }

    /**
     * Sets the cancelBlock true
     */
    public void cancelBlock() {
        this.cancelBlock = true;
    }

    /**
     * Registers a new listener to the miner
     * @param listener New Listener
     */
    public void registerListener(MinerListener listener) {
        listeners.add(listener);
    }

}
