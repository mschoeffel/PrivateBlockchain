import api.services.AccountService;
import api.services.BlockService;
import api.services.DispatcherService;
import api.services.TransactionService;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.jaxrs.GensonJaxRSFeature;
import logic.DependencyManager;
import network.BlockchainNetwork;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import threads.Miner;

import javax.ws.rs.ApplicationPath;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Main Application of the blockchain
 */
@ApplicationPath("blockchain/api")
public class Application extends ResourceConfig {

    //Logger to show additional information
    private static Logger logger = Logger.getLogger( Application.class );

    /**
     * Creates and starts the blockchain application with miner in a different thread attached
     */
    public Application() {
        logger.info("Application: Application started.");
        packages(true, "api.services");
        registerClasses(getServiceClasses());
        register(new GensonJaxRSFeature().use(new GensonBuilder()
                .setSkipNull(true)
                .useIndentation(true)
                .useDateAsTimestamp(false)
                .useDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
                .create()));

        //Gets the blockchain form the dependency manager (So the blockchain is either created new or if already a blockchain is existing the existing one will be used)
        logger.info("Application: Get blockchain.");
        BlockchainNetwork blockchainNetwork = DependencyManager.getBlockchainNetwork();

        if (blockchainNetwork == null) {
            //Blockchain Network could not be instanciated
            logger.error("Blockchain Network could not be instantiated.");
        }

        //Creating a miner to the blockchain
        logger.info("Application: Create Miner.");
        Miner miner = DependencyManager.getMiner();
        miner.registerListener(blockchainNetwork);

        //Starting the miner
        logger.info("Application: Start Miner.");
        Thread thread = new Thread(miner);
        thread.start();
    }

    protected Set<Class<?>> getServiceClasses() {
        final Set<Class<?>> result = new HashSet<>();

        result.add(BlockService.class);
        result.add(TransactionService.class);
        result.add(DispatcherService.class);
        result.add(AccountService.class);

        return result;
    }
}
