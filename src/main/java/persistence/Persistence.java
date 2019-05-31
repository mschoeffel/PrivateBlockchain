package persistence;

import com.owlike.genson.Genson;
import models.Block;
import models.Chain;
import utils.SHA3Util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Persistence {

    private Charset encoding = StandardCharsets.UTF_8;
    private String path = "chains/";

    public Persistence(){
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
    }

    public void wirteChain(Chain chain){
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

    private boolean doesChainNotExist(int networkId){
        File file = new File(getPathToChain(networkId));
        return !file.exists();
    }

    private void createChain(int networkId){
        File file = new File(getPathToChain(networkId));
        file.mkdir();
    }

    private boolean fileDoesNotExist(int networkId, String id){
        File file = new File(getPathToBlock(networkId, id));
        return !file.exists();
    }

    private void writeBlock(Block block, int networkId, String id){
        File file = new File(getPathToBlock(networkId, id));

        try(OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file), encoding)){
            Genson genson = new Genson();
            genson.serialize(block, outputStream);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

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

    private boolean doesChainExist(int networkId){
        File file = new File(getPathToChain(networkId));
        return file.exists();
    }

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

    private String getPathToChain(int networkId){
        return path + networkId;
    }

    private String getPathToBlock(int networkId, String blockId){
        return path + networkId + "/" + blockId + ".json";
    }
}
