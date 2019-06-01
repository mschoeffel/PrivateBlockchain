package threads;

import logic.Blockchain;
import logic.DependencyManager;
import logic.PendingTransactions;
import models.Block;
import models.Transaction;
import utils.SignatureUtil;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Miner implements Runnable{

    private List<MinerListener> listeners = new ArrayList<>();
    private boolean mining = true;
    private boolean cancelBlock = false;
    private Block block;
    private UUID minerId;
    private KeyPair keyPair;

    public Miner(){
        minerId = UUID.randomUUID();
        keyPair = SignatureUtil.generateKeyPair();
        SignatureUtil.saveKeyPair(keyPair, minerId.toString());
    }

    @Override
    public void run(){
        while(isMining()){
            block = getNewBlockFromMining();

            while(!cancelBlock && doesNotFullfillDifficulty(block.getBlockHash())){
                try{
                    block.incrementNonce();
                } catch(ArithmeticException e){
                    restartMining();
                }
            }

            if(cancelBlock){
                block = null;
                cancelBlock = false;
            } else{
                blockMined(block);
            }
        }
    }

    private Block getNewBlockFromMining(){
        PendingTransactions pendingTransactions = DependencyManager.getPendingTransactions();
        Blockchain blockchain = DependencyManager.getBlockchain();
        List<Transaction> transactions = pendingTransactions.getTransactionsForNextBlock();

        return new Block(transactions, blockchain.getPreviousHash());
    }

    private boolean doesNotFullfillDifficulty(byte[] digest){
        Blockchain blockchain = DependencyManager.getBlockchain();
        return !blockchain.fulfillsDifficulty(digest);
    }

    private void restartMining(){
        PendingTransactions pendingTransactions = DependencyManager.getPendingTransactions();
        List<Transaction> transactions = pendingTransactions.getTransactionsForNextBlock();

        block .setTransactions(transactions);
    }

    private void blockMined(Block block){
        block.setCoinbase(SignatureUtil.getCoinbaseFromPublicKey(keyPair));

        if(block.getTransactions().size() > 0){
            block.getTransactions().forEach(transaction -> transaction.setBlockId(block.getBlockHash()));
        }

        DependencyManager.getPendingTransactions().clearPendingTransactions(block);
        DependencyManager.getBlockchain().addBlock(block);

        listeners.forEach(listener -> listener.notifyNewBlock(block));
    }

    public boolean isMining(){
        return mining;
    }

}
