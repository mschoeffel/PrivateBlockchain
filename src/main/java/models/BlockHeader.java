package models;

import api.converters.HashConverter;
import com.owlike.genson.annotation.JsonConverter;
import logic.Blockchain;
import org.apache.log4j.Logger;
import utils.SHA3Util;

import java.io.Serializable;

/**
 * Class that represents the header af a block
 */
public class BlockHeader implements Serializable {

    //Logger to display additional information
    Logger logger = Logger.getLogger(BlockHeader.class);
    //Version of the blockchain
    private int version = Blockchain.VERSION;
    //Nonce for the miners to modify
    private int nonce = 0;
    //Timestamp of the created block header
    private long timestamp;
    //Hash of the previous block
    private byte[] previousHash;
    //Hash of the transaction List
    private byte[] transactionListHash;

    /**
     * Creates a new empty block header
     */
    public BlockHeader() {
        logger.info("BlockHeader: BlockHeader was created.");
    }

    /**
     * Creates a new block header with a timestamp, the previous block hash and the transaction list hash
     *
     * @param timestamp           Timestamp of the block
     * @param previousHash        Hash of the previous block
     * @param transactionListHash Hash of the transaction list
     */
    public BlockHeader(long timestamp, byte[] previousHash, byte[] transactionListHash) {
        this();
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.transactionListHash = transactionListHash;
    }

    /**
     * Increments the nonce
     *
     * @throws ArithmeticException Exception if the incrementation fails
     */
    public void incrementNonce() throws ArithmeticException {
        if (this.nonce == Integer.MAX_VALUE) {
            throw new ArithmeticException("nonce too high");
        }
        this.nonce++;
        logger.info("BlockHeader: Nonce was incremented.");
    }

    //Getter Setter:

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

    @JsonConverter(HashConverter.class)
    public byte[] getPreviousHash() {
        return previousHash;
    }

    @JsonConverter(HashConverter.class)
    public void setPreviousHash(byte[] previousHash) {
        this.previousHash = previousHash;
    }

    @JsonConverter(HashConverter.class)
    public byte[] getTransactionListHash() {
        return transactionListHash;
    }

    @JsonConverter(HashConverter.class)
    public void setTransactionListHash(byte[] transactionListHash) {
        this.transactionListHash = transactionListHash;
    }

    //Serialisation:

    /**
     * Formats the object to a String and returns the String.
     *
     * @return The object as a String.
     */
    @Override
    public String toString() {
        return "BlockHeader{" +
                ", nonce=" + nonce +
                ", timestamp=" + timestamp +
                ", previousHash='" + previousHash + '\'' +
                ", transactionListHash='" + transactionListHash + '\'' +
                '}';
    }


    /**
     * Returns the hash of itself
     *
     * @return Hash of itself
     */
    public byte[] asHash() {
        return SHA3Util.hash256(this);
    }
}
