package com.basrikahveci.p2p.blockchain;

import com.basrikahveci.p2p.peer.Config;
import com.basrikahveci.p2p.peer.Peer;
import com.basrikahveci.p2p.peer.network.Connection;
import com.basrikahveci.p2p.peer.network.message.Message;

public class Transaction implements Message {

	private static final long serialVersionUID = -3196129525650361631L;

	private final String peerName;

	private final int ttl;

	private final int hops;

	private final long transactionTimeoutDurationInMillis;

	private transient long transactionStartTimestamp;

	private final byte[] publicKey;

	private final String publicKeyAlgorithm;
	
	private Block block;

	public Transaction(String peerName, int ttl, int hops,
			long transactionTimeoutDurationInMillis) {
		this.peerName = peerName;
		this.ttl = ttl;
		this.hops = hops;
		this.transactionTimeoutDurationInMillis = transactionTimeoutDurationInMillis;
		this.publicKey = Config.getPublicKey();
		this.publicKeyAlgorithm = Config.getPublicKeyAlgorithm();
	}

	public String getPeerName() {
		return peerName;
	}

	public int getTtl() {
		return ttl;
	}

	public int getHops() {
		return hops;
	}

	public long getTransactionTimeoutDurationInMillis() {
		return transactionTimeoutDurationInMillis;
	}

	public long getTransactionStartTimestamp() {
		return transactionStartTimestamp;
	}

	public void setTransactionStartTimestamp(long transactionStartTimestamp) {
		this.transactionStartTimestamp = transactionStartTimestamp;
	}

	public Transaction next() {
		return ttl > 1 ? new Transaction(peerName, ttl - 1, hops + 1,
				transactionTimeoutDurationInMillis) : null;
	}

	public void handle(Peer peer, Connection connection) {
		peer.handleTransaction(connection, this);
	}

	@Override
	public String toString() {
		return "Ping{" + "peerName=" + peerName + ", ttl=" + ttl + ", hops="
				+ hops + ", pingStartTimestamp=" + transactionStartTimestamp
				+ '}';
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
	
	

}
