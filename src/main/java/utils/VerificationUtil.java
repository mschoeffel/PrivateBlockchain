package utils;

import accounts.Account;
import logic.Blockchain;
import logic.DependencyManager;
import models.Block;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.merkle.MerkleTree;

import java.util.Arrays;

/**
 * Helper class for verification of stuff
 */
public class VerificationUtil {

    //Logger to display additional information
    private static Logger logger = Logger.getLogger(VerificationUtil.class);

    /**
     * Verifies a block
     * @param block Block to verify
     * @return Boolean if the block is valid or not
     */
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

    /**
     * Verifies a transaction (contains signature check, balance check and pending transaction check)
     * @param transaction Transaction to verify
     * @return Boolean if the transaction is valid or not
     */
    public static boolean verifyTransaction(Transaction transaction){
        boolean signatureVerified = verifySignature(transaction);
        boolean balanceVerified = verifyBalance(transaction);
        boolean pendingTransactionsVerified = verifyPendingTransactions(transaction);

        return signatureVerified && balanceVerified && pendingTransactionsVerified;
    }

    /**
     * Verifies the signature of a transaction
     * @param transaction Transaction to verify
     * @return Boolean if the signature of the transaction is valid or not
     */
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

    /**
     * Verifies a transaction to the balance of an account (sender account needs t have enough coins and doesnt go below zero)
     * @param transaction Transaction to verify
     * @return Boolean if the transaction is valid or not regarding the balance of an account
     */
    public static boolean verifyBalance(Transaction transaction){
        logger.info("Verification: Verify Balance.");
        Account account = DependencyManager.getAccountStorage().getAccount(transaction.getSender());
        double totalCost = transaction.getAmount() + (transaction.getTransactionFeeBasePrice() * Blockchain.TRANSACTION_FEE_UNITS);
        logger.info("Verification: Balance verified? " + (totalCost <= (account.getBalance() - account.getLockedBalance())));
        return totalCost <= (account.getBalance() - account.getLockedBalance());
    }

    /**
     * Verifies a transaction to the pending transactions (no double transactions)
     * @param transaction Transaction to verify
     * @return Boolean if the transaction is valid or not regarding to the pending transactions
     */
    public static boolean verifyPendingTransactions(Transaction transaction){
        logger.info("Verification: Verify pending transaction.");
        logger.info("Verification: Pending transaction verified? " + (DependencyManager.getPendingTransactions().areThereNoOtherTransactionsFor(transaction)));
        return DependencyManager.getPendingTransactions().areThereNoOtherTransactionsFor(transaction);
    }
}
