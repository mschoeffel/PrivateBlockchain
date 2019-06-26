package persistence;

import com.owlike.genson.Genson;
import models.Block;
import models.Chain;
import utils.SHA3Util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Class to handle persistent storage of the models
 * At the moment this class is not used but if you wish to store the data consistent even if the network is stopped
 * feel free to use it
 */
public class Persistence {

    private Charset encoding = StandardCharsets.UTF_8;
    private String path = "chains/";

    /**
     * Creates a new Persistence
     */
    public Persistence(){
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
    }

    /**
     * Writes the chain to the storage
     * @param chain Chain to write
     */
    public void writeChain(Chain chain){
        int networkId = chain.getNetworkId();
        if(doesChainNotExist(networkId)){
            createChain(networkId);
        }
        chain.getChain().forEach(item -> {
            String id = SHA3Util.digestToHex(item.getBlockHash());
            if(fileDoesNotExist(networkId, id)){
                writeBlock(item, networkId, id);
            }
        });
    }

    /**
     * Checks if a chain does not exist in the storage
     * @param networkId Id of the blockchain network
     * @return boolean if the chain does not exists
     */
    private boolean doesChainNotExist(int networkId){
        File file = new File(getPathToChain(networkId));
        return !file.exists();
    }

    /**
     * Creates a chain storage file
     * @param networkId Id of the blockchain network
     */
    private void createChain(int networkId){
        File file = new File(getPathToChain(networkId));
        file.mkdir();
    }

    /**
     * Checks if a file with a block storage does not exist
     * @param networkId Id of the blockchain network
     * @param id Id of a block
     * @return Boolean if the file doe not exist
     */
    private boolean fileDoesNotExist(int networkId, String id){
        File file = new File(getPathToBlock(networkId, id));
        return !file.exists();
    }

    /**
     * Writes a new block storage file
     * @param block Block to store
     * @param networkId Id of the blockchain network
     * @param id Id of the block
     */
    private void writeBlock(Block block, int networkId, String id){
        File file = new File(getPathToBlock(networkId, id));

        try(OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file), encoding)){
            Genson genson = new Genson();
            genson.serialize(block, outputStream);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Reads a chain out of the storage
     * @param networkId Id of the blockchain network
     * @return new empty chain or, if storage exists, chain read from the storage
     */
    public Chain readChain(int networkId){
        Chain chain = new Chain(networkId);
        if(doesChainExist(networkId)){
            File folder = new File(getPathToChain(networkId));
            File[] files = folder.listFiles();
            for (File file : files) {
                Block block = readBlock(file);
                chain.add(block);
            }
        }
        return chain;
    }

    /**
     * Checks if a chain storage exists
     * @param networkId Id of the blockchain network
     * @return Boolean if the chain exists
     */
    private boolean doesChainExist(int networkId){
        File file = new File(getPathToChain(networkId));
        return file.exists();
    }

    /**
     * Reads a block from a file
     * @param file File to read from
     * @return Block from the file or null
     */
    private Block readBlock(File file){
        Block block = null;

        try(InputStreamReader inputStream = new InputStreamReader(new FileInputStream(file), encoding)){
            Genson genson = new Genson();
            block = genson.deserialize(inputStream, Block.class);
        } catch(IOException e){
            e.printStackTrace();
        }
        return block;
    }

    /**
     * Returns the storage path to a chain
     * @param networkId Id of the blockchain network
     * @return Path of the storage file of a chain
     */
    private String getPathToChain(int networkId){
        return path + networkId;
    }

    /**
     * Returns the storage path to a block
     * @param networkId Id of the blockchain network
     * @param blockId Id of the block
     * @return Path to the storage file of a block
     */
    private String getPathToBlock(int networkId, String blockId){
        return path + networkId + "/" + blockId + ".json";
    }
}
