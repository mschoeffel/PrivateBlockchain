package utils;

import logic.Blockchain;
import models.Block;
import models.Transaction;
import utils.merkle.MerkleTree;

import java.util.Arrays;

public class VerificationUtil {

    public static boolean verifyBlock(Block block){
        boolean fulfillsDifficulty; //TODO
        boolean correctVesion = Blockchain.VERSION == block.getBlockHeader().getVersion();
        boolean transactionVerified = true;
        boolean merkleTreeVerified = Arrays.equals(block.getBlockHeader().getTransactionListHash(), new MerkleTree(block.getTransactions()).getMerkleTreeRoot());

        for(Transaction transaction : block.getTransactions()){
            transactionVerified = verifyTransaction(transaction);

            if(!transactionVerified){
                break;
            }
        }

        return fulfillsDifficulty && correctVesion && transactionVerified &&merkleTreeVerified;
    }

    public static boolean verifyTransaction(Transaction transaction){
        boolean signatureVerified = verifySignature(transaction);
        boolean balanceVerified = verifyBalance(transaction);
        boolean pendingTransactionsVerified = verifyPendingTransactions(transaction);

        return signatureVerified && balanceVerified && pendingTransactionsVerified;
    }
}
