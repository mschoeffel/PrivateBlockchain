package network;

import logic.DependencyManager;
import models.Block;
import network.adapters.BlockAdapter;
import network.adapters.TransactionAdapter;

public class MessageHandler {

    public MessageHandler() {
    }

    public void handleTransaction(TransactionAdapter transactionAdapter) {
        DependencyManager.getPendingTransactions().addPendingTransaction(transactionAdapter.getTransaction());
    }

    public void handleBlock(BlockAdapter blockAdapter) {
        Block block = blockAdapter.getBlock();

        DependencyManager.getBlockchain().addBlock(block);
        DependencyManager.getMiner().cancelBlock();
    }
}
