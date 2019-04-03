package models;

import utils.SHA3Util;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Class that represents a transaction of the blockchain network
 */
public class Transaction implements Serializable {

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

    public Transaction(String sender, String receiver, String data, double amount, int nonce, double transactionFeeBasePrice, double transactionFeeLimit){
        this(
                sender.getBytes(Charset.forName("utf8")),
                receiver.getBytes(Charset.forName("utf8")),
                data.getBytes(Charset.forName("utf8")),
                amount,
                nonce,
                transactionFeeBasePrice,
                transactionFeeLimit);
    }

    public Transaction(byte[] sender, byte[] receiver, byte[] data, double amount, int nonce, double transactionFeeBasePrice, double transactionFeeLimit){
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
        this.amount = amount;
        this.nonce = nonce;
        this.transactionFeeBasePrice = transactionFeeBasePrice;
        this.transactionFeeLimit = transactionFeeLimit;

        createTxId();
    }

    /**
     * Creates the txId / Hash of the Transaction.
     */
    private void createTxId(){
        this.txId = SHA3Util.hash256(this);
    }

    /**
     * Forms the Object into a String.
     * @return Object as String.
     */
    @Override
    public String toString(){
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

    //Getter and Setter

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getTxId() {
        return txId;
    }

    public void setTxId(byte[] txId) {
        this.txId = txId;
    }
}
