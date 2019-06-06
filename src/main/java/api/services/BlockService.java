package api.services;

import logic.DependencyManager;
import models.Block;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("blocks")
public class BlockService {
    @Context
    UriInfo uriInfo;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecentBlocks(@QueryParam("size") @DefaultValue("10") int size,
                                    @QueryParam("offset") @DefaultValue("0") int offset) {
        List<Block> blocks = DependencyManager.getBlockchain().getLatestBlocks(size, offset);

        return Response.ok(blocks).build();
    }

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
