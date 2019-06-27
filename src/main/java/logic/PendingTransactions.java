package logic;

import models.Block;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.SizeUtil;
import utils.TransactionComparatorByFee;

import java.util.*;

/**
 * This class stores all the pending transactions that not have been added to a block
 */
public class PendingTransactions {

    //Logger to display additional information
    private Logger logger = Logger.getLogger(PendingTransactions.class);
    //Queue of open pending transactions
    private PriorityQueue<Transaction> pendingTransactions;

    /**
     * Creates a new empty storage for pending transactions
     */
    public PendingTransactions() {
        Comparator<Transaction> comparator = new TransactionComparatorByFee();
        pendingTransactions = new PriorityQueue<>(11, comparator);
    }

    /**
     * Adds a transaction to the queue
     *
     * @param transaction transaction to add
     */
    public void addPendingTransaction(Transaction transaction) {
        logger.info("PendingTransactions: Transaction added.");
        pendingTransactions.add(transaction);
    }

    /**
     * Adds multiple transactions to the queue
     *
     * @param transactions Multiple transactions
     */
    public void addPendingTransactions(Collection<Transaction> transactions) {
        logger.info("PendingTransactions: Transactions added.");
        transactions.forEach(this::addPendingTransaction);
    }

    /**
     * Returns the next transactions for a new block
     *
     * @return List of transactions
     */
    public List<Transaction> getTransactionsForNextBlock() {
        logger.info("PendingTransactions: Receive transactions for next block.");
        List<Transaction> nextTransactions = new ArrayList<>();

        int transactionCapacity = SizeUtil.calculateTransactionCapacity();

        PriorityQueue<Transaction> temp = new PriorityQueue<>(pendingTransactions);

        while (transactionCapacity > 0 && !temp.isEmpty()) {
            nextTransactions.add(temp.poll());
            transactionCapacity--;
        }
        return nextTransactions;
    }

    /**
     * Removes a transaction from the queue
     *
     * @param transaction Transaction to remove
     */
    public void clearPendingTransaction(Transaction transaction) {
        pendingTransactions.remove(transaction);
    }

    /**
     * Removes all transactions of a block from the queue
     *
     * @param block Block with the transactions that should be removed from the queue
     */
    public void clearPendingTransactions(Block block) {
        clearPendingTransactions(block.getTransactions());
    }

    /**
     * Removes multiple transactions from the queue
     *
     * @param transactions Multiple transactions to remove
     */
    public void clearPendingTransactions(Collection<Transaction> transactions) {
        transactions.forEach(item -> pendingTransactions.remove(item));
    }

    /**
     * Checks if any pending transaction is stored in the queue
     *
     * @return Returns if there is a transaction in the queue or not
     */
    public boolean pendingTransactionsAvailable() {
        return !pendingTransactions.isEmpty();
    }

    /**
     * Checks if there are no other correlating transactions in the queue to the given transaction
     *
     * @param transaction Given transaction to check for in the queue
     * @return Returns if there are other correlating transactions in the queue
     */
    public boolean areThereNoOtherTransactionsFor(Transaction transaction) {
        boolean result = true;

        for (Transaction pendingTransaction : pendingTransactions) {
            if (pendingTransaction.equals(transaction)) {
                continue;
            }
            if (Arrays.equals(pendingTransaction.getSender(), transaction.getSender())) {
                result = false;
            }
        }
        return result;
    }
}
