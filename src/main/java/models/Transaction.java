package models;

import api.converters.HashConverter;
import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import org.apache.log4j.Logger;
import utils.SHA3Util;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * Class that represents a transaction of the blockchain network
 */
public class Transaction implements Serializable {

    Logger logger = Logger.getLogger(Transaction.class);

    private byte[] sender; //The sender of the transaction
    private byte[] receiver; //The receiver of the transaction
    private double amount; //The amount to send
    private int nonce; //The nonce of the transaction
    private double transactionFeeBasePrice; //The basis price of the transaction fee
    private double transactionFeeLimit; //The max price of the transaction fee
    private byte[] data; //The optional private data stored in the transaction

    private transient byte[] txId; //The transaction Id / Hash
    private transient long timeStamp; //Timestamp of the transaction
    private transient byte[] blockId; //Block Id / Hash of the transaction
    private transient byte[] signature; //Signature of the transaction
    private transient double transactionFee; //Actual transaction fee
    private transient int sizeInByte; //Size of the transaction

    public Transaction(String sender, String receiver, String data, double amount, int nonce, double transactionFeeBasePrice, double transactionFeeLimit) {
        this(
                sender.getBytes(Charset.forName("utf8")),
                receiver.getBytes(Charset.forName("utf8")),
                data.getBytes(Charset.forName("utf8")),
                amount,
                nonce,
                transactionFeeBasePrice,
                transactionFeeLimit);
    }

    public Transaction(byte[] sender, byte[] receiver, byte[] data, double amount, int nonce, double transactionFeeBasePrice, double transactionFeeLimit) {
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
        this.amount = amount;
        this.nonce = nonce;
        this.transactionFeeBasePrice = transactionFeeBasePrice;
        this.transactionFeeLimit = transactionFeeLimit;

        createTxId();
    }

    public Transaction(byte[] sender, byte[] receiver, double amount, int nonce, double transactionFeeBasePrice, double transactionFeeLimit) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.nonce = nonce;
        this.transactionFeeBasePrice = transactionFeeBasePrice;
        this.transactionFeeLimit = transactionFeeLimit;

        createTxId();
    }

    public Transaction() {
        createTxId();
    }

    /**
     * Creates the txId / Hash of the Transaction.
     */
    private void createTxId() {
        logger.info("Transaction: New transaction created.");
        this.txId = SHA3Util.hash256(this);
    }

    /**
     * Forms the Object into a String.
     *
     * @return Object as String.
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "sender='" + SHA3Util.digestToHex(sender) + '\'' +
                ", receiver='" + SHA3Util.digestToHex(receiver) + '\'' +
                ", data='" + SHA3Util.digestToHex(data) + '\'' +
                ", amount=" + amount +
                ", nonce=" + nonce +
                ", transactionFeeBasePrice=" + transactionFeeBasePrice +
                ", transactionFeeLimit=" + transactionFeeLimit +
                '}';
    }

    public String asJSONString() {
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);

        return "{" +
                "\"sender\":\"" + SHA3Util.digestToHex(sender) + '\"' +
                ",\"receiver\":\"" + SHA3Util.digestToHex(receiver) + '\"' +
                ",\"amount\":\"" + amount + "\"" +
                //			",\"nonce\":" + nonce +
                ",\"transactionFeeBasePrice\":\"" + df.format(transactionFeeBasePrice) + "\"" +
                ",\"transactionFeeLimit\":\"" + transactionFeeLimit + "\"" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                nonce == that.nonce &&
                Double.compare(that.transactionFeeBasePrice, transactionFeeBasePrice) == 0 &&
                Double.compare(that.transactionFeeLimit, transactionFeeLimit) == 0 &&
                Arrays.equals(txId, that.txId) &&
                Arrays.equals(sender, that.sender) &&
                Arrays.equals(receiver, that.receiver);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(amount, nonce, transactionFeeBasePrice, transactionFeeLimit);
        result = 31 * result + Arrays.hashCode(txId);
        result = 31 * result + Arrays.hashCode(sender);
        result = 31 * result + Arrays.hashCode(receiver);
        return result;
    }

    @JsonIgnore
    public String getTxIdAsString() {
        return SHA3Util.hash256AsHex(this);
    }

    //Getter and Setter

    @JsonIgnore
    public byte[] getSignature() {
        return signature;
    }

    @JsonConverter(HashConverter.class)
    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @JsonConverter(HashConverter.class)
    public byte[] getTxId() {
        return txId;
    }

    public void setTxId(byte[] txId) {
        this.txId = txId;
    }

    @JsonConverter(HashConverter.class)
    public byte[] getSender() {
        return sender;
    }

    @JsonConverter(HashConverter.class)
    public void setSender(byte[] sender) {
        this.sender = sender;
    }

    @JsonConverter(HashConverter.class)
    public byte[] getReceiver() {
        return receiver;
    }

    @JsonConverter(HashConverter.class)
    public void setReceiver(byte[] receiver) {
        this.receiver = receiver;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public double getTransactionFeeBasePrice() {
        return transactionFeeBasePrice;
    }

    public void setTransactionFeeBasePrice(double transactionFeeBasePrice) {
        this.transactionFeeBasePrice = transactionFeeBasePrice;
    }

    public double getTransactionFeeLimit() {
        return transactionFeeLimit;
    }

    public void setTransactionFeeLimit(double transactionFeeLimit) {
        this.transactionFeeLimit = transactionFeeLimit;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @JsonConverter(HashConverter.class)
    public byte[] getBlockId() {
        return blockId;
    }

    @JsonIgnore
    public void setBlockId(byte[] blockId) {
        this.blockId = blockId;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public int getSizeInByte() {
        return sizeInByte;
    }

    @JsonIgnore
    public void setSizeInByte(int sizeInByte) {
        this.sizeInByte = sizeInByte;
    }
}
