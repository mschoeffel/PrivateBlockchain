package accounts;

import logic.Blockchain;
import models.Block;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.SHA3Util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class represents an account of the blockchain
 */
public class Account {

    //Logger to display additional information
    private Logger logger = Logger.getLogger(Account.class);
    //Address of the account
    private byte[] address;
    //Current balance of the account
    private double balance;
    //Locked balance of the account
    private double lockedBalance;
    //List of incoming transactions to the account
    private List<Transaction> incomingTransaction;
    //List of outgoing transactions to the account
    private List<Transaction> outgoingTransactions;
    //List of blocks this account has mined
    private List<Block> minedBlocks;

    /**
     * Creates a new Account wit a given address
     *
     * @param address Given address
     */
    public Account(byte[] address) {
        logger.info("Account: Account created. Address: " + SHA3Util.digestToHex(address));
        this.address = address;
        this.incomingTransaction = new CopyOnWriteArrayList<>();
        this.outgoingTransactions = new CopyOnWriteArrayList<>();
        this.minedBlocks = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds an incoming transaction
     *
     * @param transaction Incoming transaction
     */
    public void addIncomingTransaction(Transaction transaction) {
        logger.info("Account: Added incoming transaction. Amount:" + transaction.getAmount());
        this.incomingTransaction.add(transaction);
        this.balance += transaction.getAmount();
    }

    /**
     * Adds an outgoing transaction
     *
     * @param transaction Outgoing transaction
     */
    public void addOutgoingTransaction(Transaction transaction) {
        logger.info("Account: Added outgoing transaction. Amount:" + transaction.getAmount());
        this.outgoingTransactions.add(transaction);
        this.balance -= transaction.getAmount();
        this.balance -= transaction.getTransactionFee();
    }

    /**
     * Adds a mined block
     *
     * @param block Mined Block
     */
    public void addMinedBlock(Block block) {
        logger.info("Account: Added block.");
        this.minedBlocks.add(block);
        this.balance += Blockchain.BLOCK_REWARD;
        this.lockedBalance += Blockchain.BLOCK_REWARD;
        for (Transaction transaction : block.getTransactions()) {
            this.balance += transaction.getTransactionFee();
            this.lockedBalance += transaction.getTransactionFee();
        }
    }

    /**
     * Adds a amount to the balance of the account
     *
     * @param amount Amount to add to the balance
     */
    public void addBalance(double amount) {
        logger.info("Account: Balance added: " + amount);
        this.balance += amount;
    }

    /**
     * Unlocks a amount of the locked balance
     *
     * @param sumToUnlock Amount to unlock
     */
    public void unlockBalance(double sumToUnlock) {
        this.lockedBalance -= sumToUnlock;
    }

    /**
     * Compares two account objects
     *
     * @param o Object to compare
     * @return Returns if the objects are the same or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Arrays.equals(address, account.address);
    }

    /**
     * Returns the hash of the account
     *
     * @return Hash of the address
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(address);
    }

    //Getter Setter:

    public byte[] getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getLockedBalance() {
        return lockedBalance;
    }

    public void setLockedBalance(double lockedBalance) {
        this.lockedBalance = lockedBalance;
    }

    public List<Transaction> getIncomingTransaction() {
        return incomingTransaction;
    }

    public void setIncomingTransaction(List<Transaction> incomingTransaction) {
        this.incomingTransaction = incomingTransaction;
    }

    public List<Transaction> getOutgoingTransactions() {
        return outgoingTransactions;
    }

    public void setOutgoingTransactions(List<Transaction> outgoingTransactions) {
        this.outgoingTransactions = outgoingTransactions;
    }

    public List<Block> getMinedBlocks() {
        return minedBlocks;
    }

    public void setMinedBlocks(List<Block> minedBlocks) {
        this.minedBlocks = minedBlocks;
    }
}
