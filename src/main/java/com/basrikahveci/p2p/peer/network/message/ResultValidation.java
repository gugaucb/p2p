package com.basrikahveci.p2p.peer.network.message;

import com.basrikahveci.p2p.blockchain.Block;
import com.basrikahveci.p2p.peer.Config;
import com.basrikahveci.p2p.peer.Peer;
import com.basrikahveci.p2p.peer.network.Connection;
import com.basrikahveci.p2p.peer.network.message.ping.Ping;
import com.basrikahveci.p2p.peer.network.message.ping.Pong;

public class ResultValidation implements Message {

	private static final long serialVersionUID = -4757891342721412497L;

	private final String pingPeerName;

	private final String senderPeerName;

	private final String peerName;

	private final String serverHost;

	private final int serverPort;

	private final int ttl;

	private final int hops;
	

	private final byte[] publicKey;

	private final String publicKeyAlgorithm;

	private boolean result;

	private Block block;

	public ResultValidation(String pingPeerName, String senderPeerName,
			String peerName, String serverHost, int serverPort, int ttl, 
			int hops, Block block, boolean result) {
		this.pingPeerName = pingPeerName;
		this.senderPeerName = senderPeerName;
		this.peerName = peerName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.ttl = ttl;
		this.hops = hops;
		this.publicKey = Config.getPublicKey();
		this.publicKeyAlgorithm = Config.getPublicKeyAlgorithm();
		this.result = result;
		this.block = block;

	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public String getPingPeerName() {
		return pingPeerName;
	}

	public String getSenderPeerName() {
		return senderPeerName;
	}

	public String getPeerName() {
		return peerName;
	}

	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getTtl() {
		return ttl;
	}

	public int getHops() {
		return hops;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public String getPublicKeyAlgorithm() {
		return publicKeyAlgorithm;
	}

	@Override
	public void handle(Peer peer, Connection connection) {
		peer.handleResultValidation(connection, this);

	}

	public ResultValidation next(String thisServerName) {
		return ttl > 1 ? new ResultValidation(pingPeerName, thisServerName, peerName, serverHost, serverPort, ttl - 1, hops + 1,this.block, this.result) : null;
	}

}
