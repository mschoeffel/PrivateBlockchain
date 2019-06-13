package utils;

import accounts.Account;
import logic.Blockchain;
import logic.DependencyManager;
import models.Block;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.merkle.MerkleTree;

import java.util.Arrays;

public class VerificationUtil {

    private static Logger logger = Logger.getLogger(VerificationUtil.class);

    public static boolean verifyBlock(Block block){
        logger.info("Verification: Verify block.");
        boolean fulfillsDifficulty = DependencyManager.getBlockchain().fulfillsDifficulty(block.getBlockHash());
        boolean correctVesion = Blockchain.VERSION == block.getBlockHeader().getVersion();
        boolean transactionVerified = true;
        boolean merkleTreeVerified = Arrays.equals(block.getBlockHeader().getTransactionListHash(), new MerkleTree(block.getTransactions()).getMerkleTreeRoot());

        for(Transaction transaction : block.getTransactions()){
            transactionVerified = verifyTransaction(transaction);

            if(!transactionVerified){
                break;
            }
        }

        logger.info("Verification: Block verified? " + (fulfillsDifficulty && correctVesion && transactionVerified &&merkleTreeVerified));
        return fulfillsDifficulty && correctVesion && transactionVerified &&merkleTreeVerified;
    }

    public static boolean verifyTransaction(Transaction transaction){
        boolean signatureVerified = verifySignature(transaction);
        boolean balanceVerified = verifyBalance(transaction);
        boolean pendingTransactionsVerified = verifyPendingTransactions(transaction);

        return signatureVerified && balanceVerified && pendingTransactionsVerified;
    }

    public static boolean verifySignature(Transaction transaction){
        logger.info("Verification: Verify signature.");
        boolean result;
        try{
            logger.debug(transaction.asJSONString());
            result = SignatureUtil.verify(transaction.asJSONString().getBytes("UTF-8"),
                    transaction.getSignature(), transaction.getSender());
            logger.debug("SignatureVerification: " + result);
        } catch(Exception e){
            result = false;
        }
        logger.info("Verification: Signature verified? " + result);
        return result;
    }

    public static boolean verifyBalance(Transaction transaction){
        logger.info("Verification: Verify Balance.");
        Account account = DependencyManager.getAccountStorage().getAccount(transaction.getSender());
        double totalCost = transaction.getAmount() + (transaction.getTransactionFeeBasePrice() * Blockchain.TRANSACTION_FEE_UNITS);
        logger.info("Verification: Balance verified? " + (totalCost <= (account.getBalance() - account.getLockedBalance())));
        return totalCost <= (account.getBalance() - account.getLockedBalance());
    }

    public static boolean verifyPendingTransactions(Transaction transaction){
        logger.info("Verification: Verify pending transaction.");
        logger.info("Verification: Pending transaction verified? " + (DependencyManager.getPendingTransactions().areThereNoOtherTransactionsFor(transaction)));
        return DependencyManager.getPendingTransactions().areThereNoOtherTransactionsFor(transaction);
    }
}
