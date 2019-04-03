/*
 * Copyright (c) Tobias Fertig, 2018.
 */

package com.blockchainvision.basicblockchain.utils.merkle;

import com.blockchainvision.basicblockchain.models.Transaction;
import com.owlike.genson.annotation.JsonConverter;
import com.blockchainvision.basicblockchain.api.converters.HashListConverter;
import com.blockchainvision.basicblockchain.utils.SHA3Helper;
import org.apache.log4j.Logger;
import org.bouncycastle.util.Arrays;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MerkleTree
{
	private static Logger logger = Logger.getLogger( MerkleTree.class );

	private MerkleTreeElement root;

	public MerkleTree( List<Transaction> transactions )
	{
		initMerkleTree( transactions );
	}

	private void initMerkleTree( List<Transaction> transactions )
	{
		logger.debug( "init tree" );

		List<MerkleTreeElement> elements = new ArrayList<>( );

		for ( Transaction transaction : transactions )
		{
			logger.debug( "adding transactions" );
			elements.add( new MerkleTreeElement( transaction.getTxId( ) ) );
		}

		while ( elements.size( ) > 1 )
		{
			logger.debug( "building next layer" );
			elements = getNextLayer( elements );
		}

		if(elements.size() == 1)
		{
			root = elements.get( 0 );
		} else
		{
			root = new MerkleTreeElement( SHA3Helper.hash256( ( Serializable ) transactions ) );
		}

		logger.debug( "finished init: " + SHA3Helper.digestToHex( root.getHash( ) ) );
	}

	private List<MerkleTreeElement> getNextLayer( List<MerkleTreeElement> elements )
	{
		List<MerkleTreeElement> nextLayer = new ArrayList<>( );

		for ( int i = 0; i < elements.size( ); i += 2 )
		{
			MerkleTreeElement left = elements.get( i );
			MerkleTreeElement right = ( i == elements.size( ) - 1 ) ? elements.get( i ) : elements.get( i + 1 );
			byte[] nextHash = SHA3Helper.hash256( Arrays.concatenate( left.getHash( ), right.getHash( ) ) );
			MerkleTreeElement parent = new MerkleTreeElement( left, right, nextHash );
			left.setParent( parent );
			right.setParent( parent );

			nextLayer.add( parent );
		}

		return nextLayer;
	}

	public byte[] getMerkleTreeRoot( )
	{
		return root.getHash( );
	}

	@JsonConverter( HashListConverter.class )
	public List<byte[]> getHashesForTransactionHash( byte[] hash )
	{
		List<byte[]> hashList = new CopyOnWriteArrayList<>( );

		checkChild( root, hash, hashList );

		hashList.add( root.getHash( ) );

		return hashList;
	}

	private boolean checkChild( MerkleTreeElement child, byte[ ] hash, List<byte[]> hashList )
	{
		boolean result = java.util.Arrays.equals( child.getHash( ), hash );

		if ( child.hasChilds( ) )
		{
			MerkleTreeElement left = child.getLeft( );
			MerkleTreeElement right = child.getRight( );

			if ( checkChild( left, hash, hashList ) )
			{
				result = true;
				hashList.add( right.getHash( ) );
			}

			if ( checkChild( right, hash, hashList ) )
			{
				result = true;
				hashList.add( left.getHash( ) );
			}
		}

		return result;
	}
}
