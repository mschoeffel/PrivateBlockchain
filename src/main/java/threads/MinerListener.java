package threads;

import models.Block;

public interface MinerListener {
    void notifyNewBlock(Block block);
}
