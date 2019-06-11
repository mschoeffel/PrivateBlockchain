package utils.merkle;

import models.Transaction;
import org.apache.log4j.Logger;
import org.bouncycastle.util.Arrays;
import utils.SHA3Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A basic implementation of a merkle tree
 */
public class MerkleTree {

    private static Logger logger = Logger.getLogger(MerkleTree.class); //Logger to log some status messages
    private MerkleTreeElement root; //The root element of the merkle tree

    public MerkleTree(List<Transaction> transactions) {
        initMerkleTree(transactions);
    }

    /**
     * Setting up the merkle tree.
     *
     * @param transactions List of transactions to store in the tree.
     */
    private void initMerkleTree(List<Transaction> transactions) {
        logger.debug("MerkleTree: Init.");

        List<MerkleTreeElement> elements = new ArrayList<>();

        for (Transaction transaction : transactions) {
            logger.debug("MerkleTree: Adding transactions.");
            elements.add(new MerkleTreeElement(transaction.getTxId()));
        }

        while (elements.size() > 1) {
            logger.debug("MerkleTree: Building next layer.");
            elements = getNextLayer(elements);
        }

        if (elements.size() == 1) {
            root = elements.get(0);
        } else {
            root = new MerkleTreeElement(SHA3Util.hash256((Serializable) transactions));
        }

        logger.debug("MerkleTree: Finished init: " + SHA3Util.digestToHex(root.getHash()));
    }

    /**
     * Returns the next layer of the merkle tree to a list of elements.
     *
     * @param elements Elements of which you wanna get the next layer.
     * @return Returns the next layer of the parameter elements.
     */
    private List<MerkleTreeElement> getNextLayer(List<MerkleTreeElement> elements) {
        List<MerkleTreeElement> nextLayer = new ArrayList<>();

        for (int i = 0; i < elements.size(); i += 2) {
            MerkleTreeElement left = elements.get(i);
            MerkleTreeElement right = (i == elements.size() - 1) ? elements.get(i) : elements.get(i + 1);
            byte[] nextHash = SHA3Util.hash256(Arrays.concatenate(left.getHash(), right.getHash()));
            MerkleTreeElement parent = new MerkleTreeElement(left, right, nextHash);
            left.setParent(parent);
            right.setParent(parent);

            nextLayer.add(parent);
        }

        return nextLayer;
    }

    public List<byte[]> getHashesForTransactionHash(byte[] hash) {
        List<byte[]> hashList = new CopyOnWriteArrayList<>();

        checkChild(root, hash, hashList);

        hashList.add(root.getHash());

        return hashList;
    }

    private boolean checkChild(MerkleTreeElement child, byte[] hash, List<byte[]> hashList) {
        boolean result = java.util.Arrays.equals(child.getHash(), hash);

        if (child.hasChilds()) {
            MerkleTreeElement left = child.getLeft();
            MerkleTreeElement right = child.getRight();

            if (checkChild(left, hash, hashList)) {
                result = true;
                hashList.add(left.getHash());
            }

            if (checkChild(right, hash, hashList)) {
                result = true;
                hashList.add(right.getHash());
            }
        }
        return result;
    }

    /**
     * Returns the hash of the root element.
     *
     * @return Hash of the root element.
     */
    public byte[] getMerkleTreeRoot() {
        return root.getHash();
    }
}
