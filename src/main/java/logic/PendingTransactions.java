package logic;

import models.Block;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.SizeUtil;
import utils.TransactionComparatorByFee;

import java.util.*;

public class PendingTransactions {

    Logger logger = Logger.getLogger(PendingTransactions.class);
    private PriorityQueue<Transaction> pendingTransactions;

    public PendingTransactions(){
        Comparator<Transaction> comparator = new TransactionComparatorByFee();
        pendingTransactions = new PriorityQueue<>(11, comparator);
    }

    public void addPendingTransaction(Transaction transaction){
        logger.info("PendingTransactions: Transaction added.");
        pendingTransactions.add(transaction);
    }

    public void addPendingTransactions(Collection<Transaction> transactions){
        logger.info("PendingTransactions: Transactions added.");
        transactions.forEach(this::addPendingTransaction);
    }

    public List<Transaction> getTransactionsForNextBlock(){
        logger.info("PendingTransactions: Receive transactions for next block.");
        List<Transaction> nextTransactions = new ArrayList<>();

        int transactionCapacity = SizeUtil.calculateTransactionCapacity();

        PriorityQueue<Transaction> temp = new PriorityQueue<>(pendingTransactions);

        while(transactionCapacity > 0 && !temp.isEmpty()){
            nextTransactions.add(temp.poll());
            transactionCapacity--;
        }
        return nextTransactions;
    }

    public void clearPendingTransaction(Transaction transaction){
        pendingTransactions.remove(transaction);
    }

    public void clearPendingTransactions(Block block){
        clearPendingTransactions(block.getTransactions());
    }

    public void clearPendingTransactions(Collection<Transaction> transactions){
        transactions.forEach(item -> pendingTransactions.remove(item));
    }

    public boolean pendingTransactionsAvailable(){
        return !pendingTransactions.isEmpty();
    }

    public boolean areThereNoOtherTransactionsFor(Transaction transaction){
        boolean result = true;

        for (Transaction pendingTransaction : pendingTransactions) {
            if(pendingTransaction.equals(transaction)){
                continue;
            }
            if(Arrays.equals(pendingTransaction.getSender(), transaction.getSender())){
                result = false;
            }
        }
        return result;
    }

}
