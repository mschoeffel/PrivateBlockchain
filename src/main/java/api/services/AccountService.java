package api.services;

import accounts.Account;
import logic.DependencyManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("accounts")
public class AccountService {

    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{address}")
    public Response getAccountByAddress(@PathParam("address") String address) {
        Account account = DependencyManager.getAccountStorage().getAccount(address);

        Response response;

        if (account == null) {
            response = Response.status(404).build();
        } else {
            response = Response.ok(account).build();
        }

        return response;
    }
}
