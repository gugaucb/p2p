package com.basrikahveci.p2p.blockchain;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import com.basrikahveci.p2p.peer.network.message.Payload;

public class BlockChain {
	
	private Stack<Block> chain;
	
	private static BlockChain blockChain;
	
	private BlockChain() {
		chain = new Stack<Block>();
	}
	
	public static BlockChain getInstance(){
		if(blockChain==null){
			blockChain = new BlockChain();
			if(blockChain.getChain().size()==0){
				blockChain.createGenesisBlock();
			}
		}
		return blockChain;
	}
	
	public Collection<Block> getChain(){
		return Collections.unmodifiableCollection(chain);
	}
	private void createGenesisBlock(){
		chain.add(new Block(0L, System.currentTimeMillis(),new Payload(),"0"));
	}
	
	public Block getLastBlock(){
		return chain.peek();
	}
	
	public void addBlock(Block block){
		chain.push(block);
	}
	
	public boolean isChainValid(){
		for (int i = 1; i < chain.size(); i++) {
			Block currentBlock = chain.get(i);
			Block previousBlock = chain.get(i-1);
			
			if(currentBlock.getHash()!=currentBlock.calculateHash()){
				return false;
			}
			
			if(currentBlock.getPreviousHash()!= previousBlock.getHash()){
				return false;
			}
			
			
		}
		return true;
	}

	public void removeBlock(com.basrikahveci.p2p.blockchain.Block block) {
		if(chain.contains(block)){
			int index = chain.indexOf(block);
			int quantPop = (chain.size()-index);
			for (int i = 0; i < quantPop; i++) {
				chain.pop();
			}
		}
		
	}
	
}
