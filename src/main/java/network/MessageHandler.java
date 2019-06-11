package network;

import logic.DependencyManager;
import models.Block;
import network.adapters.BlockAdapter;
import network.adapters.TransactionAdapter;
import org.apache.log4j.Logger;

public class MessageHandler {
    Logger logger = Logger.getLogger(MessageHandler.class);

    public MessageHandler() {
    }

    public void handleTransaction(TransactionAdapter transactionAdapter) {
        logger.info("Handler: New transaction received.");
        DependencyManager.getPendingTransactions().addPendingTransaction(transactionAdapter.getTransaction());
    }

    public void handleBlock(BlockAdapter blockAdapter) {
        logger.info("Handler: New block received.");
        Block block = blockAdapter.getBlock();

        DependencyManager.getBlockchain().addBlock(block);
        DependencyManager.getMiner().cancelBlock();
    }
}
