package com.basrikahveci.p2p.peer.network.message;

import org.json.JSONStringer;

import com.basrikahveci.p2p.blockchain.Block;
import com.basrikahveci.p2p.peer.Peer;
import com.basrikahveci.p2p.peer.network.Connection;
import com.basrikahveci.p2p.peer.network.message.ping.Ping;

public class RequestValidation implements Message {

	private final String peerName;
	
	private transient int ttl;

	private transient int hops;

	private transient long blockTimeoutDurationInMillis;

	private transient long blockStartTimestamp;
	
	private Block block;
	
	public RequestValidation(String peerName, int ttl, int hops, long blockTimeoutDurationInMillis, long blockStartTimestamp, Block block) {
		this.peerName = peerName;
		this.ttl = ttl;
		this.blockTimeoutDurationInMillis = blockTimeoutDurationInMillis;
		this.blockStartTimestamp = blockStartTimestamp;
		this.block = block;
	}

	public RequestValidation(String peerName, int ttl, int hops, long blockTimeoutDurationInMillis,  Block block) {
		this.peerName = peerName;
		this.ttl = ttl;
		this.blockTimeoutDurationInMillis = blockTimeoutDurationInMillis;
		this.block = block;
	}
	
	
	public Block getBlock() {
		return block;
	}




	public void setBlock(Block block) {
		this.block = block;
	}




	public String getPeerName() {
		return peerName;
	}




	public void setBlockTimeoutDurationInMillis(long blockTimeoutDurationInMillis) {
		this.blockTimeoutDurationInMillis = blockTimeoutDurationInMillis;
	}




	public long getBlockStartTimestamp() {
		return blockStartTimestamp;
	}

	public void setBlockStartTimestamp(long blockStartTimestamp) {
		this.blockStartTimestamp = blockStartTimestamp;
	}

	public long getBlockTimeoutDurationInMillis() {
		return blockTimeoutDurationInMillis;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public void setHops(int hops) {
		this.hops = hops;
	}

	public int getTtl() {
		return ttl;
	}

	public int getHops() {
		return hops;
	}
	
	@Override
	public void handle(Peer peer, Connection connection) {
		peer.handleRequestBlock(connection, this);

	}
	
	@Override
	public String toString() {
		String obj = JSONStringer.valueToString(this);
		return obj;
	}
	
	 public RequestValidation next() {
	        return ttl > 1 ? new RequestValidation(peerName, ttl-1, hops+1, blockTimeoutDurationInMillis, block):null;
	    }

}
