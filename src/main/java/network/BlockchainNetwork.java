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

/**
 * The main blockchain network
 */
public class BlockchainNetwork extends ReceiverAdapter implements MinerListener {

    //Logger to show some additional information
    private static Logger logger = Logger.getLogger(BlockchainNetwork.class);
    //Genson to serialize and deserialize objects
    private static Genson genson = new Genson();
    //Communication channel (network channel)
    private JChannel channel;
    //View for the channel
    private View view;
    //Message handler for the channel
    private MessageHandler handler;

    /**
     * Creates a new blockchain network with network channel etc.
     *
     * @throws Exception Exception if the network cant be started
     */
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

    /**
     * Receives a message and handles the message -> deserialization of the message
     *
     * @param msg Message
     */
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

    /**
     * Registering a new view to the network
     *
     * @param view View to register
     */
    @Override
    public void viewAccepted(View view) {
        if (this.view == null) {
            view.forEach(System.out::println);
        } else {
            List<Address> newMembers = View.newMembers(this.view, view);
            logger.info("BlockchainNetwork: View: New members: ");
            newMembers.forEach(System.out::println);

            List<Address> exMembers = View.leftMembers(this.view, view);
            logger.info("BlockchainNetwork: View: Exited members:");
            exMembers.forEach(System.out::println);
        }
        this.view = view;
    }

    /**
     * Serializes the blockchain network state
     *
     * @param output Stream where to serialize to
     */
    @Override
    public void getState(OutputStream output) {
        genson.serialize(new BlockchainAdapter(DependencyManager.getBlockchain()), output);
    }

    /**
     * Sets the blockchain network state
     *
     * @param input Inputstream to read from
     */
    @Override
    public void setState(InputStream input) {
        BlockchainAdapter blockchainAdapter = genson.deserialize(input, BlockchainAdapter.class);

        DependencyManager.injectBlockchain(blockchainAdapter.getBlockchain());
        DependencyManager.getAccountStorage();
    }

    /**
     * Sends a transaction
     *
     * @param transaction Transaction to send
     * @throws Exception Exception if the Transaction couldn't be sent
     */
    public void sendTransaction(Transaction transaction) throws Exception {
        Message message = new Message(null, transactionToJSON(transaction));
        channel.send(message);
    }

    /**
     * Serializes a transaction to JSON
     *
     * @param transaction Transaction to serialize
     * @return Serialized transaction in JSON as byte Array
     */
    private byte[] transactionToJSON(Transaction transaction) {
        return genson.serializeBytes(new TransactionAdapter(transaction));
    }

    /**
     * Sends a block to the channel
     *
     * @param block Block to send
     * @throws Exception Exception if the sending went wrong
     */
    public void sendBlock(Block block) throws Exception {
        Message message = new Message(null, blockToJSON(block));
        channel.send(message);
    }

    /**
     * Serializes a block to JSON
     *
     * @param block Block to serialize
     * @return Serialized block in JSON as byte Array
     */
    private byte[] blockToJSON(Block block) {
        return genson.serializeBytes(new BlockAdapter(block));
    }

    /**
     * Notifies a block to the network
     *
     * @param block Block to notify
     */
    @Override
    public void notifyNewBlock(Block block) {
        try {
            sendBlock(block);
        } catch (Exception e) {
            logger.error("Could not send block: " + block);
            e.printStackTrace();
        }
    }
}
