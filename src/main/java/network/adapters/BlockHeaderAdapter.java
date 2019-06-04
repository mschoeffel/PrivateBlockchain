package network.adapters;

import api.converters.HashConverter;
import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import models.BlockHeader;

public class BlockHeaderAdapter {
    private BlockHeader blockHeader;

    public BlockHeaderAdapter() {
        this.blockHeader = new BlockHeader();
    }

    public BlockHeaderAdapter(BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public String getType() {
        return "BlockHeaderAdapter";
    }

    @JsonIgnore
    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public int getVersion() {
        return this.blockHeader.getVersion();
    }

    public void setVersion(int version) {
        this.blockHeader.setVersion(version);
    }

    public int getNonce() {
        return this.blockHeader.getNonce();
    }

    public void setNonce(int nonce) {
        this.blockHeader.setNonce(nonce);
    }

    public long getTimestamp() {
        return this.blockHeader.getTimestamp();
    }

    public void setTimestamp(long timestamp) {
        this.blockHeader.setTimestamp(timestamp);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getPreviousHash() {
        return this.blockHeader.getPreviousHash();
    }

    @JsonConverter(HashConverter.class)
    public void setPreviousHash(byte[] previousHash) {
        this.blockHeader.setPreviousHash(previousHash);
    }

    @JsonConverter(HashConverter.class)
    public byte[] getTransactionListHash() {
        return this.blockHeader.getTransactionListHash();
    }

    @JsonConverter(HashConverter.class)
    public void setTransactionListHash(byte[] transactionListHash) {
        this.blockHeader.setTransactionListHash(transactionListHash);
    }
}
