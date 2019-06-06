package logic;

import accounts.AccountStorage;
import network.BlockchainNetwork;
import threads.Miner;

public class DependencyManager {

    private static PendingTransactions pendingTransactions;
    private static Blockchain blockchain;
    private static Miner miner;
    private static AccountStorage accountStorage;
    private static BlockchainNetwork blockchainNetwork;

    public static PendingTransactions getPendingTransactions() {
        if (pendingTransactions == null) {
            pendingTransactions = new PendingTransactions();
        }
        return pendingTransactions;
    }

    public static Blockchain getBlockchain() {
        if (blockchain == null) {
            blockchain = new Blockchain();
        }
        return blockchain;
    }

    public static void injectBlockchain(Blockchain blockchain) {
        DependencyManager.blockchain = blockchain;
    }

    public static Miner getMiner() {
        if (miner == null) {
            miner = new Miner();
        }
        return miner;
    }

    public static AccountStorage getAccountStorage() {
        if (accountStorage == null) {
            accountStorage = new AccountStorage();
        }
        return accountStorage;
    }

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
