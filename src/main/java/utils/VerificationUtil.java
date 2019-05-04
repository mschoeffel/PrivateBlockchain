package utils;

import logic.Blockchain;
import models.Block;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.merkle.MerkleTree;

import java.util.Arrays;

public class VerificationUtil {

    private static Logger logger = Logger.getLogger(VerificationUtil.class);

    public static boolean verifyBlock(Block block){
        boolean fulfillsDifficulty = true; //TODO
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

    public static boolean verifySignature(Transaction transaction){
        boolean result;
        try{
            logger.debug(transaction.asJSONString());
            result = SignatureUtil.verify(transaction.asJSONString().getBytes("UTF-8"),
                    transaction.getSignature(), transaction.getSender());
            logger.debug("SignatureVerification: " + result);
        } catch(Exception e){
            result = false;
        }
        return result;
    }

    public static boolean verifyBalance(Transaction transaction){
        //TODO
        return true;
    }

    public static boolean verifyPendingTransactions(Transaction transaction){
        //TODO
        return true;
    }
}
