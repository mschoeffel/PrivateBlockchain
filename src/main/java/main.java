import models.Transaction;

public class main {

    public static void main(String[] args){
        Transaction transaction = new Transaction("this_is_the_sender", "this_is_the_reveiver", 10.0D, 22, 7.0D, 8.0D);
        System.out.println(transaction.toString());
    }
}
