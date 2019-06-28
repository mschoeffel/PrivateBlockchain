package utils;

import models.Transaction;

import java.util.Comparator;

/**
 * Comparator class to compare two transactions depending on their fee
 */
public class TransactionComparatorByFee implements Comparator<Transaction> {

    /**
     * Compares two given transactions depending on their base fee price
     * @param o1 Transaction one
     * @param o2 Transaction two
     * @return Int compare between the two transactions
     */
    @Override
    public int compare(Transaction o1, Transaction o2) {
        int result = 0;

        if (o2.getTransactionFeeBasePrice() - o1.getTransactionFeeBasePrice() < 0.0) {
            result = -1;
        } else if (o2.getTransactionFeeBasePrice() - o1.getTransactionFeeBasePrice() > 0.0) {
            result = 1;
        }

        return result;
    }
}
