package models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenesisBlock extends Block {

    Logger logger = Logger.getLogger(GenesisBlock.class);
    private static byte[] ZERO_HASH = new byte[32];
    private static byte[] ZERO_ACCOUNT = new byte[65];

    static{
        for(byte b : ZERO_HASH){
            b = 0;
        }
        for(byte b : ZERO_ACCOUNT){
            b = 0;
        }
    }

    private Map<String, Double> accountBalances;

    public GenesisBlock(){
        super(ZERO_HASH);
        logger.info("GenesisBlock: New Genesisblock created.");
        this.setCoinbase(ZERO_ACCOUNT);
        this.accountBalances = new ConcurrentHashMap<>();
        initializeAccounts();
    }

    private void initializeAccounts(){
        try{
            final Reader in = new InputStreamReader(this.getClass().getResourceAsStream("initialAccounts.csv"), "UTF-8");
            final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(';').parse(in);

            for(final CSVRecord record : records){
                accountBalances.put(record.get("account"), Double.parseDouble(record.get("balance")));
            }
        } catch(final Exception e){
            logger.error("GenesisBlock: Error initializing accounts.", e);
            e.printStackTrace();
        }
    }

    public Map<String, Double> getAccountBalances() {
        return accountBalances;
    }
}
