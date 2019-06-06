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

@Path("/")
public class DispatcherService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hash}")
    public Response searchForHash(@PathParam("hash") String hash) {
        Response response;

        if (hash.length() == 130) {
            Account account = DependencyManager.getAccountStorage().getAccount(hash);

            if (account == null) {
                response = Response.status(404).build();
            } else {
                response = Response.ok(account).build();
            }
        } else {
            Transaction transaction;
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
