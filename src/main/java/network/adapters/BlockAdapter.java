package network.adapters;

import api.converters.HashConverter;
import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import models.Block;
import models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class BlockAdapter {
    private Block block;

    public BlockAdapter() {
        this.block = new Block();
    }

    public BlockAdapter(Block block) {
        this.block = block;
    }

    public String getType() {
        return "BlockAdapter";
    }

    @JsonIgnore
    public Block getBlock() {
        return block;
    }

    public int getMagicNumber() {
        return this.block.getMagicNumber();
    }

    public void setMagicNumber(int magicNumber) {
        this.block.setMagicNumber(magicNumber);
    }

    public int getBlockSize() {
        return this.block.getBlockSize();
    }

    public void setBlockSize(int blockSize) {
        this.block.setBlockSize(blockSize);
    }

    public int getTransactionCount() {
        return this.block.getTransactionCount();
    }

    public void setTransactionCount(int transactionCount) {
        this.block.setTransactionCount(transactionCount);
    }

    public int getBlockNumber() {
        return this.block.getBlockNumber();
    }

    public void setBlockNumber(int blockNumber) {
        this.block.setBlockNumber(blockNumber);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getCoinbase() {
        return this.block.getCoinbase();
    }

    @JsonConverter(HashConverter.class)
    public void setCoinbase(byte[] coinbase) {
        this.block.setCoinbase(coinbase);
    }

    public List<TransactionAdapter> getTransactions() {
        List<TransactionAdapter> transactions = new ArrayList<>();

        for (Transaction transaction : this.block.getTransactions()) {
            transactions.add(new TransactionAdapter(transaction));
        }

        return transactions;
    }

    public void setTransactions(List<TransactionAdapter> transactions) {
        List<Transaction> realTransactions = new ArrayList<>();

        for (TransactionAdapter transaction : transactions) {
            realTransactions.add(transaction.getTransaction());
        }

        this.block.setTransactions(realTransactions);
    }

    public BlockHeaderAdapter getBlockHeader() {
        return new BlockHeaderAdapter(this.block.getBlockHeader());
    }

    public void setBlockHeader(BlockHeaderAdapter blockHeader) {
        this.block.setBlockHeader(blockHeader.getBlockHeader());
    }
}
