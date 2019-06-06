package api.services;

import logic.DependencyManager;
import models.Block;
import models.Transaction;
import utils.SHA3Util;
import utils.VerificationUtil;
import utils.merkle.MerkleTree;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Path("transactions")
public class TransactionService {

    @Context
    UriInfo uriInfo;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendTransaction(Transaction transaction){
        if(VerificationUtil.verifyTransaction(transaction)){
            DependencyManager.getPendingTransactions().addPendingTransaction(transaction);
            DependencyManager.getMiner().cancelBlock();

            try{
                DependencyManager.getBlockchainNetwork().sendTransaction(transaction);
                return Response.created(new URI(uriInfo.getRequestUriBuilder().path(transaction.getTxIdAsString()).toString())).build();
            } catch(URISyntaxException e){
                throw new WebApplicationException("Uri for transaction could not be generated.");
            } catch(Exception e){
                throw new WebApplicationException(e);
            }
        } else{
            throw new WebApplicationException(422);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hash}")
    public Response getTransactionByHash(@PathParam("hash") String hash){
        Transaction transaction = DependencyManager.getBlockchain().getTransactionByHash(hash);

        Response response;

        if(transaction == null){
            response = Response.status(404).build();
        } else{
            response = Response.ok(transaction).header("Link",
                    uriInfo.getBaseUriBuilder()
                            .path("blocks")
                            .path(SHA3Util.digestToHex(transaction.getBlockId())))
                    .build();
        }
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecentTransactions(@QueryParam("size") @DefaultValue("10") int size,
                                          @QueryParam("offset") @DefaultValue("0") int offset){
        List<Transaction> transactions = new ArrayList<>();

        DependencyManager.getBlockchain().getLatestBlocks(size, offset).forEach(latestBlock -> {
            transactions.addAll(latestBlock.getTransactions());
        });

        return Response.ok(transactions).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hash}/merkle")
    public Response getMerkleTreeHashesForTransaction(@PathParam("hash") String hash){
        Transaction transaction = DependencyManager.getBlockchain().getTransactionByHash(hash);

        Response response;

        if(transaction == null){
            response = Response.status(404).build();
        } else{
            Block block = DependencyManager.getBlockchain().getBlockByHash(transaction.getBlockId());
            MerkleTree merkleTree = new MerkleTree(block.getTransactions());
            response = Response.ok(merkleTree.getHashesForTransactionHash(transaction.getTxId())).build();
        }

        return response;
    }

}
