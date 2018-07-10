package com.basrikahveci.p2p.blockchain;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.json.JSONStringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basrikahveci.p2p.peer.network.message.Payload;

public class Block {
	private static final Logger LOGGER = LoggerFactory.getLogger(Block.class);
	
	private Long index;
	
	private Long timestamp;
	
	private Payload payload;
	
	private String previousHash;
	
	private String hash;
	

	public Block(Long index, Long timestamp, Payload payload,
			String previousHash) {
		super();
		this.index = index;
		this.timestamp = timestamp;
		this.payload = payload;
		this.previousHash = previousHash;
		this.hash = calculateHash();
		
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	


	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}



	public String calculateHash() {
		String hash = "";
		String json = JSONStringer.valueToString(payload);
		String message = getIndex() + getTimestamp() + getPreviousHash() + json;

		byte[] data;
		try {
			data = message.getBytes("UTF8");
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashByte = digest.digest(data);
			hash = Base64.getEncoder().encodeToString(hashByte);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			LOGGER.error("Fail calculate hash", e);
		}

		return hash;
	}

	@Override
	public String toString() {
		String obj = JSONStringer.valueToString(this);
		return obj;
	}

}
