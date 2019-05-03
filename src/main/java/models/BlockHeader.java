package models;

import logic.Blockchain;
import utils.SHA3Util;

import java.io.Serializable;

/**
 * Class that represents the header af a block
 */
public class BlockHeader implements Serializable {

    private int version = Blockchain.VERSION;
    private int nonce = 0;
    private long timestamp;
    private byte[] previousHash;
    private byte[] transactionListHash;

    public BlockHeader() {}

    public BlockHeader(long timestamp, byte[] previousHash, byte[] transactionListHash) {
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.transactionListHash = transactionListHash;
    }

    public void incrementNonce()throws ArithmeticException{
        if(this.nonce == Integer.MAX_VALUE){
            throw new ArithmeticException("nonce too high");
        }
        this.nonce++;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(byte[] previousHash) {
        this.previousHash = previousHash;
    }

    public byte[] getTransactionListHash() {
        return transactionListHash;
    }

    public void setTransactionListHash(byte[] transactionListHash) {
        this.transactionListHash = transactionListHash;
    }

    /**
     * Formats the object to a String and returns the String.
     * @return The object as a String.
     */
    @Override public String toString() {
        return "BlockHeader{" +
                ", nonce=" + nonce +
                ", timestamp=" + timestamp +
                ", previousHash='" + previousHash + '\'' +
                ", transactionListHash='" + transactionListHash + '\'' +
                '}';
    }

    public byte[] asHash(){
        return SHA3Util.hash256(this);
    }
}
