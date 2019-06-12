package network;

import com.owlike.genson.Genson;
import logic.DependencyManager;
import models.Block;
import models.Transaction;
import network.adapters.BlockAdapter;
import network.adapters.BlockchainAdapter;
import network.adapters.TransactionAdapter;
import org.apache.log4j.Logger;
import org.jgroups.*;
import threads.MinerListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class BlockchainNetwork extends ReceiverAdapter implements MinerListener {

    private static Logger logger = Logger.getLogger(BlockchainNetwork.class);
    private static Genson genson = new Genson();
    private JChannel channel;
    private View view;
    private MessageHandler handler;

    public BlockchainNetwork() throws Exception {
        handler = new MessageHandler();

        System.setProperty("java.net.preferIPv$Stack", "true");

        this.channel = new JChannel("src/main/resources/udp.xml");
        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);
        channel.connect("PrivateBlockchain");
        channel.getState(null, 0);

        logger.info("BlockchainNetwork: BlockchainNetwork started: " + channel.getAddressAsString());
    }

    @Override
    public void receive(Message msg) {
        try {
            String json = new String(msg.getRawBuffer());
            if (json.contains("\"type\":\"BlockAdapter\"")) {
                handler.handleBlock(genson.deserialize(json, BlockAdapter.class));
            } else if (json.contains("\"type\":\"TransactionAdapter\"")) {
                handler.handleTransaction(genson.deserialize(json, TransactionAdapter.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View view) {
        if (this.view == null) {
            view.forEach(System.out::println);
        } else {
            List<Address> newMembers = View.newMembers(this.view, view);
            System.out.println("New members: ");
            newMembers.forEach(System.out::println);

            List<Address> exMembers = View.leftMembers(this.view, view);
            System.out.println("Exited members:");
            exMembers.forEach(System.out::println);
        }
        this.view = view;
    }

    @Override
    public void getState(OutputStream output){
        genson.serialize(new BlockchainAdapter(DependencyManager.getBlockchain()), output);
    }

    @Override
    public void setState(InputStream input){
        BlockchainAdapter blockchainAdapter = genson.deserialize(input, BlockchainAdapter.class);

        DependencyManager.injectBlockchain(blockchainAdapter.getBlockchain());
        DependencyManager.getAccountStorage();
    }

    public void sendTransaction(Transaction transaction) throws Exception{
        Message message = new Message(null, transactionToJSON(transaction));
        channel.send(message);
    }

    private byte[] transactionToJSON(Transaction transaction){
        return genson.serializeBytes(new TransactionAdapter(transaction));
    }

    public void sendBlock(Block block) throws Exception{
        Message message = new Message(null, blockToJSON(block));
        channel.send(message);
    }

    private byte[] blockToJSON(Block block){
        return genson.serializeBytes(new BlockAdapter(block));
    }

    @Override
    public void notifyNewBlock(Block block){
        try{
            sendBlock(block);
        } catch(Exception e){
            logger.error("Could not sen block: " + block);
            e.printStackTrace();
        }
    }
}
