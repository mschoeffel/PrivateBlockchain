package threads;

import models.Block;

/**
 * Interface for all the listeners to a miner
 */
public interface MinerListener {

    //Method the miner will call when a new, valid block got mined
    void notifyNewBlock(Block block);
}
