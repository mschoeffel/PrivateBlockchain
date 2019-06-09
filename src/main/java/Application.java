import api.services.AccountService;
import api.services.BlockService;
import api.services.DispatcherService;
import api.services.TransactionService;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.jaxrs.GensonJaxRSFeature;
import logic.DependencyManager;
import network.BlockchainNetwork;
import org.glassfish.jersey.server.ResourceConfig;
import threads.Miner;

import javax.ws.rs.ApplicationPath;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("blockchain/api")
public class Application extends ResourceConfig {

    public Application() {
        packages(true, "de.etherbasics.basisblockchain.api.services");
        registerClasses(getServiceClasses());
        register(new GensonJaxRSFeature().use(new GensonBuilder()
                .setSkipNull(true)
                .useIndentation(true)
                .useDateAsTimestamp(false)
                .useDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
                .create()));

        BlockchainNetwork blockchainNetwork = DependencyManager.getBlockchainNetwork();

        if (blockchainNetwork == null) {
            //Blockchain Network could not be instanciated
        }

        Miner miner = DependencyManager.getMiner();
        miner.registerListener(blockchainNetwork);

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
