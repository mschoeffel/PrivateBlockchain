package models;

import api.converters.HashConverter;
import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import org.apache.log4j.Logger;
import utils.SizeUtil;
import utils.merkle.MerkleTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a block of the blockchain network.
 */
public class Block {

    //Logger to display additional information
    private Logger logger = Logger.getLogger(Block.class);
    //Magic number
    private int magicNumber = 0xD9B4BEF9;
    //Size of the block
    private int blockSize;
    //Number of transactions per block
    private int transactionCount;
    //Number of the block
    private int blockNumber;
    //Coinbase of the block
    private byte[] coinbase;
    //List of transactions
    private List<Transaction> transactions;
    //Header of the block
    private BlockHeader blockHeader;

    /**
     * Creates a new empty block
     */
    public Block() {
        logger.info("Block: Block created.");
    }

    /**
     * Creates a new block depending on the previous block
     *
     * @param previousHash Previous block hash
     */
    public Block(byte[] previousHash) {
        this();
        this.transactionCount = 0;
        this.transactions = new ArrayList<>();
        this.blockSize = SizeUtil.calculateBlockSize(this);

        this.blockHeader = new BlockHeader(System.currentTimeMillis(), previousHash, getTransactionHash());
    }

    /**
     * Creates a new block depending on the previous block with a given list of transactions
     *
     * @param transactions List of transactions
     * @param previousHash Previous block hash
     */
    public Block(List<Transaction> transactions, byte[] previousHash) {
        this();
        this.transactions = transactions;
        this.transactionCount = transactions.size();
        this.blockSize = SizeUtil.calculateBlockSize(this);
        this.blockHeader = new BlockHeader(System.currentTimeMillis(), previousHash, getTransactionHash());
    }

    /**
     * Saves the transactions in a merkle tree and keeps the hash of the root element.
     *
     * @return Hash of the root element of the merkle tree as Bytearray.
     */
    private byte[] getTransactionHash() {
        return new MerkleTree(transactions).getMerkleTreeRoot();
    }

    /**
     * Adds a transaction to the block
     *
     * @param transaction Transaction to add
     */
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.transactionCount++;

        this.blockHeader.setTransactionListHash(getTransactionHash());
        this.blockSize = SizeUtil.calculateBlockSize(this);
    }

    /**
     * Increments the nonce of the block
     *
     * @throws ArithmeticException Exception if the increment fails
     */
    public void incrementNonce() throws ArithmeticException {
        this.blockHeader.incrementNonce();
    }

    //Getter Setter:

    @JsonIgnore
    public int getNonce() {
        return this.blockHeader.getNonce();
    }

    @JsonIgnore
    public void setNonce(int nonce) {
        this.blockHeader.setNonce(nonce);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getBlockHash() {
        return blockHeader.asHash();
    }

    //Getter and Setter
    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public byte[] getCoinbase() {
        return coinbase;
    }

    public void setCoinbase(byte[] coinbase) {
        this.coinbase = coinbase;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }
}
