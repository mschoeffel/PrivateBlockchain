package models;

import java.io.Serializable;

/**
 * Class that represents the header af a block
 */
public class BlockHeader implements Serializable {

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
}
