package threads;

import logic.DependencyManager;
import logic.PendingTransactions;
import models.Block;
import models.Transaction;
import org.junit.Before;
import org.junit.Test;
import utils.SHA3Util;

public class MinerTests implements MinerListener{
    @Before
    public void setUp()
    {
        PendingTransactions transactions = DependencyManager.getPendingTransactions( );

        for(int i = 0; i < 100; i++)
        {
            String sender = "testSender" + i;
            String receiver = "testReceiver" + i;
            double amount = i * 1.1;
            int nonce = i;
            double transactionFee = 0.0000001 * i;
            double transactionFeeLimit = 10.0;

            transactions.addPendingTransaction(
                    new Transaction( sender.getBytes( ),
                            receiver.getBytes( ),
                            amount,
                            nonce,
                            transactionFee,
                            transactionFeeLimit ) );
        }
    }

    @Test
    public void testMiner()
    {
        Miner miner = new Miner( );
        miner.registerListener( this );

        Thread thread = new Thread( miner );
        thread.start( );

        while ( DependencyManager.getPendingTransactions( ).pendingTransactionsAvailable( ) )
        {
            try
            {
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace( );
            }
        }

        miner.stopMining( );

        System.out.println( "Länge der Blockchain: " + DependencyManager.getBlockchain( ).size( ) );
    }

    @Override public void notifyNewBlock( Block block )
    {
        System.out.println( "new block mined" );

        System.out.println( SHA3Util.digestToHex( block.getBlockHash( ) ) );
    }
}
