package accounts;

import logic.Blockchain;
import models.Block;
import models.Transaction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Account {

    private byte[] address;
    private double balance;
    private double lockedBalance;
    private List<Transaction> incomingTransaction;
    private List<Transaction> outgoingTransactions;
    private List<Block> minedBlocks;

    public Account(byte[] address){
        this.address = address;
        this.incomingTransaction = new CopyOnWriteArrayList<>();
        this.outgoingTransactions = new CopyOnWriteArrayList<>();
        this.minedBlocks = new CopyOnWriteArrayList<>();
    }

    public void addIncomingTransaction(Transaction transaction){
        this.incomingTransaction.add(transaction);
        this.balance += transaction.getAmount();
    }

    public void addOutgoingTransaction(Transaction transaction){
        this.outgoingTransactions.add(transaction);
        this.balance -= transaction.getAmount();
        this.balance -= transaction.getTransactionFee();
    }

    public void addMinedBlock(Block block){
        this.minedBlocks.add(block);
        this.balance += Blockchain.BLOCK_REWARD;
        this.lockedBalance += Blockchain.BLOCK_REWARD;
        for(Transaction transaction : block.getTransactions()){
            this.balance += transaction.getTransactionFee();
            this.lockedBalance += transaction.getTransactionFee();
        }
    }

    public void addBalance(double amount){
        this.balance += amount;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Account account = (Account) o;
        return Arrays.equals(address, account.address);
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(address);
    }

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
