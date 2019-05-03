package models;

import utils.SizeUtil;
import utils.merkle.MerkleTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a block of the blockchain network.
 */
public class Block {

    private int magicNumber = 0xD9B4BEF9; //Magic number
    private int blockSize; //Size of the block
    private int transactionCount; //Number of transactions
    private int blockNumber; //Number of the Block
    private byte[] coinbase;

    private List<Transaction> transactions;

    private BlockHeader blockHeader;

    public Block() {}

    public Block(byte[] previousHash) {
        this.transactionCount = 0;
        this.transactions = new ArrayList<>();
        this.blockSize = SizeUtil.calculateBlockSize(this);

        this.blockHeader = new BlockHeader(System.currentTimeMillis(), previousHash, getTransactionHash());
    }

    public Block(List<Transaction> transactions, byte[] previousHash) {
        this.transactions = transactions;
        this.transactionCount = transactions.size( );
        this.blockSize = SizeUtil.calculateBlockSize( this );
        this.blockHeader = new BlockHeader(System.currentTimeMillis(), previousHash, getTransactionHash());
    }

    /**
     * Saves the transactions in a merkle tree and keeps the hash of the root element.
     * @return Hash of the root element of the merkle tree as Bytearray.
     */
    private byte[] getTransactionHash() {
        return new MerkleTree(transactions).getMerkleTreeRoot();
    }

    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
        this.transactionCount++;

        this.blockHeader.setTransactionListHash(getTransactionHash());
        this.blockSize = SizeUtil.calculateBlockSize(this);
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
