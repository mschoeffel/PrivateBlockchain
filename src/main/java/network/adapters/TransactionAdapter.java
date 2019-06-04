package network.adapters;

import api.converters.HashConverter;
import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import models.Transaction;

public class TransactionAdapter {
    private Transaction transaction;

    public TransactionAdapter() {
        this.transaction = new Transaction();
    }

    public TransactionAdapter(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getType() {
        return "TransactionAdapter";
    }

    @JsonIgnore
    public Transaction getTransaction() {
        return transaction;
    }

    @JsonConverter(HashConverter.class)
    public byte[] getTxId() {
        return this.transaction.getTxId();
    }

    @JsonConverter(HashConverter.class)
    public void setTxId(byte[] txId) {
        this.transaction.setTxId(txId);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getSender() {
        return this.transaction.getSender();
    }

    @JsonConverter(HashConverter.class)
    public void setSender(byte[] sender) {
        this.transaction.setSender(sender);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getReceiver() {
        return this.transaction.getReceiver();
    }

    @JsonConverter(HashConverter.class)
    public void setReceiver(byte[] receiver) {
        this.transaction.setReceiver(receiver);
    }

    public double getAmount() {
        return this.transaction.getAmount();
    }

    public void setAmount(double amount) {
        this.transaction.setAmount(amount);
    }

    public int getNonce() {
        return this.transaction.getNonce();
    }

    public void setNonce(int nonce) {
        this.transaction.setNonce(nonce);
    }

    public long getReceivedTimeStamp() {
        return this.transaction.getTimeStamp();
    }

    public void setReceivedTimeStamp(long receivedTimeStamp) {
        this.transaction.setTimeStamp(receivedTimeStamp);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getBlockId() {
        return this.transaction.getBlockId();
    }

    @JsonConverter(HashConverter.class)
    public void setBlockId(byte[] blockId) {
        this.transaction.setBlockId(blockId);
    }

    public double getTransactionFeeBasePrice() {
        return transaction.getTransactionFeeBasePrice();
    }

    public void setTransactionFeeBasePrice(double transactionFeeBasePrice) {
        this.transaction.setTransactionFeeBasePrice(transactionFeeBasePrice);
    }

    public double getTransactionFeeLimit() {
        return transaction.getTransactionFeeLimit();
    }

    public void setTransactionFeeLimit(double transactionFeeLimit) {
        this.transaction.setTransactionFeeLimit(transactionFeeLimit);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getSignature() {
        return transaction.getSignature();
    }

    @JsonConverter(HashConverter.class)
    public void setSignature(byte[] signature) {
        this.transaction.setSignature(signature);
    }

    public double getTransactionFee() {
        return transaction.getTransactionFee();
    }

    public void setTransactionFee(double transactionFee) {
        this.transaction.setTransactionFee(transactionFee);
    }

    public int getSizeInByte() {
        return transaction.getSizeInByte();
    }

    public void setSizeInByte(int sizeInByte) {
        this.transaction.setSizeInByte(sizeInByte);
    }
}
