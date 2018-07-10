package com.basrikahveci.p2p.peer.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cripto {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Cripto.class);
	
	private static KeyPairGenerator keyGen;
	
	private KeyPair pair;
	
	private PrivateKey priv;
	
	private PublicKey pub;
	
	private static Cripto cripto;
	
	private Cripto() {
		try {
			init();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			LOGGER.error("Fail Key Pair Generator", e);
		}
	}

	public static Cripto getInstance(){
		if(cripto==null){
			cripto = new Cripto();
		}
		return cripto;
	}
	
	private void init() throws NoSuchAlgorithmException, NoSuchProviderException {
		keyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		generateKeyPair();
	}
	
	private void generateKeyPair(){
		pair = keyGen.generateKeyPair();
	}
	
	public PublicKey getPublicKey(){
		if(pub==null){
			 pub = pair.getPublic();
		}
		return pub;
	}
	
	public PrivateKey getPrivateKey(){
		if(priv==null){
			priv = pair.getPrivate();
		}
		return priv;
		
	}

	public String printKey(KeyPair keyPair) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
