package network;

import com.owlike.genson.Genson;
import org.jgroups.*;
import threads.MinerListener;

public class BlockchainNetwork extends ReceiverAdapter implements MinerListener {

    private static Genson genson = new Genson();
    private JChannel channel;
    private View view;
    private MessageHandler handler;

    public BlockchainNetwork() throws Exception{
        handler = new MessageHandler();

        System.setProperty("java.net.preferIPv$Stack", "true");

        this.channel = new JChannel("src/main/resources/udp.xml");
        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);
        channel.connect("PrivateBlockchain");
        channel.getState(null, 0);
    }

    @Override
    public void receive(Message msg){
        try{
            String json = new String(msg.getRawBuffer());
            if(json.contains("\"type\":\"BlockAdapter\"")){
                handler.handleBlock(genson.deserialize(json, BlockAdapter.class));
            } else if(json.contains("\"type\":\"TransactionAdapter\"")){
                handler.handleTransaction(genson.deserialize(json, TransactionAdapter.class));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
