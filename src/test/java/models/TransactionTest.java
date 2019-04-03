package models;

import org.junit.Assert;
import org.junit.Test;

public class TransactionTest {

    @Test
    public void testTransactionString(){
        Transaction transaction = new Transaction("this_is_the_sender", "this_is_the_reveiver", 10.0D, 22, 7.0D, 8.0D);
        String actual = transaction.toString();
        String expected = "Transaction{sender='746869735f69735f7468655f73656e646572', receiver='746869735f69735f7468655f7265766569766572', amount=10.0, nonce=22, transactionFeeBasePrice=7.0, transactionFeeLimit=8.0}";
        Assert.assertEquals(expected, actual);
    }

}
