package network;

import logic.DependencyManager;
import models.Block;
import network.adapters.BlockAdapter;
import network.adapters.TransactionAdapter;
import org.apache.log4j.Logger;

/**
 * Message handler to handle the messages sent in the network
 */
public class MessageHandler {

    //Logger to show some additional information
    Logger logger = Logger.getLogger(MessageHandler.class);

    /**
     * Creates a new Message Handler
     */
    public MessageHandler() {
    }

    /**
     * Handles a new transaction
     * @param transactionAdapter New received transaction adapter with a transaction
     */
    public void handleTransaction(TransactionAdapter transactionAdapter) {
        logger.info("Handler: New transaction received.");
        DependencyManager.getPendingTransactions().addPendingTransaction(transactionAdapter.getTransaction());
    }

    /**
     * Handles a new block
     * @param blockAdapter New received block adapter with a block
     */
    public void handleBlock(BlockAdapter blockAdapter) {
        logger.info("Handler: New block received.");
        Block block = blockAdapter.getBlock();

        DependencyManager.getBlockchain().addBlock(block);
        DependencyManager.getMiner().cancelBlock();
    }
}
