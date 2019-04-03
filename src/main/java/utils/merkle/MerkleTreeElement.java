/*
 * Copyright (c) Tobias Fertig, 2018.
 */

package com.blockchainvision.basicblockchain.utils.merkle;

public class MerkleTreeElement
{
	private MerkleTreeElement parent;

	private MerkleTreeElement left;

	private MerkleTreeElement right;

	private byte[] hash;

	public MerkleTreeElement( MerkleTreeElement parent, byte[] hash )
	{
		this.parent = parent;
		this.hash = hash;
	}

	public MerkleTreeElement( MerkleTreeElement left, MerkleTreeElement right, byte[] hash )
	{
		this.left = left;
		this.right = right;
		this.hash = hash;
	}

	public MerkleTreeElement( byte[] hash )
	{
		this.hash = hash;
		this.left = null;
		this.right = null;
	}

	public boolean hasChilds( )
	{
		return left != null || right != null;
	}

	public boolean hasParent( )
	{
		return parent != null;
	}

	public MerkleTreeElement getParent( )
	{
		return parent;
	}

	public void setParent( MerkleTreeElement parent )
	{
		this.parent = parent;
	}

	public MerkleTreeElement getLeft( )
	{
		return left;
	}

	public void setLeft( MerkleTreeElement left )
	{
		this.left = left;
	}

	public MerkleTreeElement getRight( )
	{
		return right;
	}

	public void setRight( MerkleTreeElement right )
	{
		this.right = right;
	}

	public byte[] getHash( )
	{
		return hash;
	}

	public void setHash( byte[] hash )
	{
		this.hash = hash;
	}
}
