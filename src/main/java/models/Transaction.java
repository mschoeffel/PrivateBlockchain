package models;

import utils.SHA3Helper;

import java.io.Serializable;
import java.nio.charset.Charset;

public class Transaction implements Serializable {

    private byte[] sender;
    private byte[] receiver;
    private double amount;
    private int nonce;
    private double transactionFeeBasePrice;
    private double transactionFeeLimit;

    private transient byte[] txId;
    private transient long timeStamp;
    private transient byte[] blockId;
    private transient byte[] signature;
    private transient double transactionFee;
    private transient int sizeInByte;

    public Transaction(String sender, String receiver, double amount, int nonce, double transactionFeeBasePrice, double transactionFeeLimit){
        this(
                sender.getBytes(Charset.forName( "utf8" )),
                receiver.getBytes(Charset.forName( "utf8" )),
                amount,
                nonce,
                transactionFeeBasePrice,
                transactionFeeLimit);
    }

    public Transaction(byte[] sender, byte[] receiver, double amount, int nonce, double transactionFeeBasePrice, double transactionFeeLimit){
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.nonce = nonce;
        this.transactionFeeBasePrice = transactionFeeBasePrice;
        this.transactionFeeLimit = transactionFeeLimit;

        createTxId();
    }

    private void createTxId(){
        this.txId = SHA3Helper.hash256(this);
    }

    @Override
    public String toString(){
        return "Transaction{" +
                "sender='" + SHA3Helper.digestToHex( sender ) + '\'' +
                ", receiver='" + SHA3Helper.digestToHex( receiver ) + '\'' +
                ", amount=" + amount +
                ", nonce=" + nonce +
                ", transactionFeeBasePrice=" + transactionFeeBasePrice +
                ", transactionFeeLimit=" + transactionFeeLimit +
                '}';
    }
}
