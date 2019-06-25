package logic;

import accounts.AccountStorage;
import network.BlockchainNetwork;
import threads.Miner;

/**
 * This is a helper class to manage all the dependency's and return them if they are already existing and if not create them first.
 */
public class DependencyManager {

    private static PendingTransactions pendingTransactions;
    private static Blockchain blockchain;
    private static Miner miner;
    private static AccountStorage accountStorage;
    private static BlockchainNetwork blockchainNetwork;

    /**
     * Returns the pending transactions of the blockchain
     * @return Pending transactions
     */
    public static PendingTransactions getPendingTransactions() {
        if (pendingTransactions == null) {
            pendingTransactions = new PendingTransactions();
        }
        return pendingTransactions;
    }

    /**
     * Returns the blockchain of the network
     * @return Blockchain
     */
    public static Blockchain getBlockchain() {
        if (blockchain == null) {
            blockchain = new Blockchain();
        }
        return blockchain;
    }

    /**
     * Injects a blockchain into the dependency manager
     * @param blockchain New blockchain
     */
    public static void injectBlockchain(Blockchain blockchain) {
        DependencyManager.blockchain = blockchain;
    }

    /**
     * Returns the miner of the application
     * @return Miner
     */
    public static Miner getMiner() {
        if (miner == null) {
            miner = new Miner();
        }
        return miner;
    }

    /**
     * Returns the account storage
     * @return Account storage
     */
    public static AccountStorage getAccountStorage() {
        if (accountStorage == null) {
            accountStorage = new AccountStorage();
        }
        return accountStorage;
    }

    /**
     * Returns the blockchain network
     * @return Blockchain network
     */
    public static BlockchainNetwork getBlockchainNetwork() {
        if (blockchainNetwork == null) {
            try {
                blockchainNetwork = new BlockchainNetwork();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return blockchainNetwork;
    }

}
