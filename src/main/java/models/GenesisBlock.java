package models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that represents the genesis (first) block of a blockchain
 */
public class GenesisBlock extends Block {

    //Logger to display additional information
    private Logger logger = Logger.getLogger(GenesisBlock.class);
    //Zero starting hash
    private static byte[] ZERO_HASH = new byte[32];
    //Zero starting account
    private static byte[] ZERO_ACCOUNT = new byte[65];

    //Filling the zero hashes with zeros
    static {
        for (byte b : ZERO_HASH) {
            b = 0;
        }
        for (byte b : ZERO_ACCOUNT) {
            b = 0;
        }
    }

    //Starting account balances
    private Map<String, Double> accountBalances;

    /**
     * Creates a new genesis block
     */
    public GenesisBlock() {
        super(ZERO_HASH);
        logger.info("GenesisBlock: New Genesisblock created.");
        this.setCoinbase(ZERO_ACCOUNT);
        this.accountBalances = new ConcurrentHashMap<>();
        initializeAccounts();
    }

    /**
     * Initializes the first accounts and balances from the accountBalances map
     */
    private void initializeAccounts() {
        try {
            final Reader in = new InputStreamReader(this.getClass().getResourceAsStream("/initialAccounts.csv"), "UTF-8");
            final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(';').parse(in);

            for (final CSVRecord record : records) {
                accountBalances.put(record.get("account"), Double.parseDouble(record.get("balance")));
            }
        } catch (final Exception e) {
            logger.error("GenesisBlock: Error initializing accounts.", e);
            e.printStackTrace();
        }
    }

    //Getter Setter:

    public Map<String, Double> getAccountBalances() {
        return accountBalances;
    }
}
