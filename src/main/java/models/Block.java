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

    //Getter and Setter

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
