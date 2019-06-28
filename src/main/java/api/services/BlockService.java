package api.services;

import logic.DependencyManager;
import models.Block;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * REST service for blocks
 */
@Path("blocks")
public class BlockService {
    @Context
    UriInfo uriInfo;

    /**
     * Returns the block to a given hash
     *
     * @param hash Hash to get the block to
     * @return Block to the given hash
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hash}")
    public Response getBlockByHash(@PathParam("hash") String hash) {
        Block block = DependencyManager.getBlockchain().getBlockByHash(hash);

        Response response;

        if (block == null) {
            response = Response.status(404).build();
        } else {
            response = Response.ok(block).build();
        }
        return response;
    }

    /**
     * Returns a list of blocks from the chain with a given start index and size
     *
     * @param size   Amount of blocks you want to get
     * @param offset Offset of the zero index
     * @return List f blocks from the given offset till the offset + size
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecentBlocks(@QueryParam("size") @DefaultValue("10") int size,
                                    @QueryParam("offset") @DefaultValue("0") int offset) {
        List<Block> blocks = DependencyManager.getBlockchain().getLatestBlocks(size, offset);

        return Response.ok(blocks).build();
    }

    /**
     * Returns the next/child block to a given blockhash
     *
     * @param hash Blockhash to get the child from
     * @return Child/Next block from the given one
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hash}/child")
    public Response getChildOfHash(@PathParam("hash") String hash) {
        Block block = DependencyManager.getBlockchain().getBlockByHash(hash);

        Response response;
        if (block == null) {
            response = Response.status(404).build();
        } else {
            response = Response.ok(DependencyManager.getBlockchain().getChildOfBlock(block)).build();
        }

        return response;
    }
}
