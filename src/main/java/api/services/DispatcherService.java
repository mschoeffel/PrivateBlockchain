package api.services;

import accounts.Account;
import logic.DependencyManager;
import models.Block;
import models.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;

/**
 * REST dispatcher service
 */
@Path("/")
public class DispatcherService {

    /**
     * Returns a block, account or transaction to a given hash and identifies by itself if the hash is a block a transaction or an account
     *
     * @param hash Given hash
     * @return Block, account or transaction
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hash}")
    public Response searchForHash(@PathParam("hash") String hash) {
        Response response = null;

        if (hash.length() == 130) {
            Account account = DependencyManager.getAccountStorage().getAccount(hash);

            if (account == null) {
                response = Response.status(404).build();
            } else {
                response = Response.ok(account).build();
            }
        } else {
            Transaction transaction = null;
            Block block = DependencyManager.getBlockchain().getBlockByHash(hash);

            if (block == null) {
                transaction = DependencyManager.getBlockchain().getTransactionByHash(hash);

                if (transaction == null) {
                    response = Response.status(404).build();
                } else {
                    response = Response.ok(transaction).build();
                }
            } else {
                response = Response.ok(block).build();
            }
        }
        return response;
    }
}
