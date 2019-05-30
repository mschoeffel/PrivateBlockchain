package logic;

public class DependencyManager {

    private static PendingTransactions pendingTransactions;
    private static Blockchain blockchain;

    public static PendingTransactions getPendingTransactions(){
        if(pendingTransactions == null){
            pendingTransactions = new PendingTransactions();
        }
        return pendingTransactions;
    }

    public static Blockchain getBlockchain(){
        if(blockchain == null){
            blockchain = new Blockchain();
        }
        return blockchain;
    }

}
