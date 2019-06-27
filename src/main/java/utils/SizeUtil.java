package utils;

import logic.Blockchain;
import models.Block;
import models.Transaction;

import java.util.List;

/**
 * Helperclass to calculate the size of objects
 */
public final class SizeUtil {

    //The  size of the meta data of a transaction in bytes
    public final static int TRANSACTION_META_DATA_SIZE_IN_BYTES = 242;
    //The maximum size of a signature of a transaction in bytes
    private final static int TRANSACTION_SIGNATURE_MAX_SIZE = 72;
    //The size of a blockheader in bytes
    public final static int BLOCK_HEADER_SIZE_IN_BYTES = 80;
    //The size of the meta data of a block in bytes
    public final static int BLOCK_META_DATA_SIZE_IN_BYTES = 81;

    private SizeUtil() {
    }

    /**
     * Calculates the size of a block.
     *
     * @param block Block to calculate.
     * @return Size of the given Block.
     */
    public static int calculateBlockSize(Block block) {
        return BLOCK_HEADER_SIZE_IN_BYTES + calculateTransactionListSize(block.getTransactions()) +
                BLOCK_META_DATA_SIZE_IN_BYTES;
    }

    /**
     * Calculates the size of a list of transactions.
     *
     * @param transactions List of transaction.
     * @return Size of the list of transactions.
     */
    public static int calculateTransactionListSize(List<Transaction> transactions) {
        int size = 0;

        for (Transaction transaction : transactions) {
            size += calculateTransactionSize(transaction);
        }

        return size;
    }

    /**
     * Calculates the size of a transaction object.
     *
     * @param transaction The transaction object.
     * @return The size of the given transaction object.
     */
    private static int calculateTransactionSize(Transaction transaction) {
        return TRANSACTION_META_DATA_SIZE_IN_BYTES + transaction.getSignature().length;
    }

    /**
     * Calculates the possible capacity of a transaction
     *
     * @return Capacity of a transaction
     */
    public static int calculateTransactionCapacity() {
        return (Blockchain.MAX_BLOCK_SIZE_BYTES - BLOCK_META_DATA_SIZE_IN_BYTES - BLOCK_HEADER_SIZE_IN_BYTES) / (TRANSACTION_META_DATA_SIZE_IN_BYTES + TRANSACTION_SIGNATURE_MAX_SIZE);
    }
}
