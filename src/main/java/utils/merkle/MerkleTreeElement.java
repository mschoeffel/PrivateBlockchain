package utils.merkle;

/**
 * Basic implementation of an element of a merkle tree
 */
public class MerkleTreeElement {
    //Parent node
    private MerkleTreeElement parent;
    //Left node
    private MerkleTreeElement left;
    //Right node
    private MerkleTreeElement right;
    //Own hash
    private byte[] hash;

    /**
     * Creates a new MerkleTreeElement to a given parent element with a hash
     *
     * @param parent Parent node
     * @param hash   Hash of the new element
     */
    public MerkleTreeElement(MerkleTreeElement parent, byte[] hash) {
        this.parent = parent;
        this.hash = hash;
    }

    /**
     * Creates a new MerkleTreeElement with a given left and right element and hash
     *
     * @param left  Left node
     * @param right Right node
     * @param hash  Hash of the new element
     */
    public MerkleTreeElement(MerkleTreeElement left, MerkleTreeElement right, byte[] hash) {
        this.left = left;
        this.right = right;
        this.hash = hash;
    }

    /**
     * Creates a new MerkleTreeElement wit a given hash
     *
     * @param hash Hash of the new element
     */
    public MerkleTreeElement(byte[] hash) {
        this.hash = hash;
        this.left = null;
        this.right = null;
    }

    /**
     * Returns if the element has children
     *
     * @return Boolean if the element has children
     */
    public boolean hasChilds() {
        return left != null || right != null;
    }

    /**
     * Returns if the element has a parent node
     *
     * @return Boolean if the element has a parent node
     */
    public boolean hasParent() {
        return parent != null;
    }

    //Getter Setter:

    public MerkleTreeElement getParent() {
        return parent;
    }

    public void setParent(MerkleTreeElement parent) {
        this.parent = parent;
    }

    public MerkleTreeElement getLeft() {
        return left;
    }

    public void setLeft(MerkleTreeElement left) {
        this.left = left;
    }

    public MerkleTreeElement getRight() {
        return right;
    }

    public void setRight(MerkleTreeElement right) {
        this.right = right;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }
}
