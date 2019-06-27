package accounts;

import logic.Blockchain;
import logic.DependencyManager;
import models.Block;
import models.GenesisBlock;
import models.Transaction;
import org.apache.log4j.Logger;
import utils.SHA3Util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents the storage of the accounts
 */
public class AccountStorage {

    //Logger to display additional information
    Logger logger = Logger.getLogger(AccountStorage.class);
    //Map of all the registered accounts
    private Map<String, Account> accounts;
    //Map of all the registered miner
    private Map<Integer, Account> minerMap;
    //Map of all the blocks of the chain
    private Map<Integer, Block> blockMap;

    /**
     * Creates a new empty account storage
     */
    public AccountStorage() {
        accounts = new ConcurrentHashMap<>();
        minerMap = new ConcurrentHashMap<>();
        blockMap = new ConcurrentHashMap<>();
        initAccounts();
        logger.info("AccountStorage: New AccountStorage created.");
    }

    /**
     * Initializes the accounts from the genesis block of the blockchain and all the blocks already created
     */
    private void initAccounts() {
        ((GenesisBlock) DependencyManager.getBlockchain().getGenesisBlock()).getAccountBalances().forEach((key, value) -> {
            Account account = getAccount(key);
            account.addBalance(value);
        });
        DependencyManager.getBlockchain().getChain().getChain().forEach(this::parseBlock);
    }

    /**
     * Parses a block of the blockchain to get the accounts of it and add the accounts and block and releases the balance of the blocks
     *
     * @param block Block to parse
     */
    public void parseBlock(Block block) {
        Account account = getAccount(block.getCoinbase());
        account.addMinedBlock(block);
        minerMap.put(block.getBlockNumber(), account);
        blockMap.put(block.getBlockNumber(), block);
        releaseBlockedBalances(block.getBlockNumber());
        parseTransactions(block.getTransactions());
    }

    /**
     * Releases the locked balance of a block if its enough times checked
     *
     * @param blockNumber Number of the block to check
     */
    private void releaseBlockedBalances(int blockNumber) {
        int key = blockNumber - Blockchain.REQUIRED_BLOCK_CONFIRMATIONS;
        Account account = minerMap.get(key);
        if (account != null) {
            double sumToUnlock = Blockchain.BLOCK_REWARD;
            Block block = blockMap.get(blockNumber);

            if (block != null) {
                for (Transaction transaction : block.getTransactions()) {
                    sumToUnlock += transaction.getTransactionFee();
                }
                blockMap.remove(blockNumber);
            }
            account.unlockBalance(sumToUnlock);
            minerMap.remove(blockNumber);
        }
    }

    /**
     * Parses the given transactions and adds the receiver and sender from the accounts
     *
     * @param transactions List of transactions to parse
     */
    private void parseTransactions(List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            Account senderAccount = getAccount(transaction.getSender());
            Account receiverAccount = getAccount(transaction.getReceiver());
            senderAccount.addOutgoingTransaction(transaction);
            receiverAccount.addIncomingTransaction(transaction);
        });
    }

    /**
     * Returns an account depending on the address
     *
     * @param address Address of the account as String
     * @return
     */
    public Account getAccount(String address) {
        Account account = accounts.get(address);
        if (account == null) {
            account = createAccount(SHA3Util.hexToDigest(address));
            accounts.put(address, account);
        }
        return account;
    }

    /**
     * Returns an account depending on the address
     *
     * @param address Address of the account as byte Array
     * @return
     */
    public Account getAccount(byte[] address) {
        String addressAsHex = SHA3Util.digestToHex(address);
        Account account = accounts.get(addressAsHex);
        if (account == null) {
            account = createAccount(address);
            accounts.put(addressAsHex, account);
        }
        return account;
    }

    /**
     * Creates a new account with a given address
     *
     * @param address Address as byte Array
     * @return Returns teh new created account
     */
    private Account createAccount(byte[] address) {
        return new Account(address);
    }
}
