package com.basrikahveci.p2p.peer.service;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basrikahveci.p2p.blockchain.Block;
import com.basrikahveci.p2p.blockchain.BlockChain;
import com.basrikahveci.p2p.blockchain.Transaction;
import com.basrikahveci.p2p.peer.Config;
import com.basrikahveci.p2p.peer.network.Connection;
import com.basrikahveci.p2p.peer.network.message.ResultValidation;
import com.basrikahveci.p2p.peer.network.message.ping.Pong;

public class TransactionService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TransactionService.class);
	
	private final Config config;

	private final ConnectionService connectionService;
	
	private final Map<String, TransactionContext> currentTransaction = new HashMap<String, TransactionContext>();
	
	public TransactionService(ConnectionService connectionService, Config config) {
		this.connectionService = connectionService;
		this.config = config;
	}
	/**
	 * Handles a Block operation initiated by another node. If the received
	 * {@link Block} message is allowed to be propagated, this peer also
	 * propagates it to its own neighbours. Additionally, it sends a
	 * {@link Pong} message back to the neighbour that has sent the {@link Transaction}
	 * message to this peer.
	 *
	 * @param bindAddress
	 *            Network address that this peer bind
	 * @param connection
	 *            Connection of the neighbour that sent the Ping message
	 * @param ping
	 *            Ping message that is received. It can be initiated by the
	 *            neighbour with the given connection or any other peer that has
	 *            no direct connection to this peer
	 */
	public void handleTransaction(InetSocketAddress bindAddress,
			Connection connection, Transaction transaction) {

		final String requestValidationPeerName = transaction
				.getPeerName();

		if (requestValidationPeerName.equals(connection.getPeerName())) {
			LOGGER.info("Handling {} of initiator {} with ttl={}",
					transaction, requestValidationPeerName,
					transaction.getTtl());
		} else {
			LOGGER.info(
					"Handling {} of initiator {} and forwarder {} with ttl={} and hops={}",
					transaction, requestValidationPeerName,
					connection.getPeerName(), transaction.getTtl(),
					transaction.getHops());
		}
		com.basrikahveci.p2p.blockchain.Block block = transaction.getBlock();
		BlockChain.getInstance().addBlock(block);
		if (BlockChain.getInstance().isChainValid()) {

			transaction
					.setTransactionStartTimestamp(System.currentTimeMillis());

			final ResultValidation resultValidation = new ResultValidation(
					requestValidationPeerName, config.getPeerName(),
					config.getPeerName(), bindAddress.getAddress()
							.getHostAddress(), bindAddress.getPort(),
					transaction.getHops() + 1, 0,block, true);
			connection.send(resultValidation);
			final Transaction next = transaction.next();
			if (next != null) {
				for (Connection neighbour : connectionService.getConnections()) {
					if (!neighbour.equals(connection)
							&& !neighbour.getPeerName().equals(
									transaction.getPeerName())) {
						LOGGER.info("Forwarding {} to {} for initiator {}",
								transaction, neighbour.getPeerName(),
								transaction.getPeerName());
						neighbour.send(transaction);
					}
				}
			}
		}else{
			final ResultValidation resultValidation = new ResultValidation(
					requestValidationPeerName, config.getPeerName(),
					config.getPeerName(), bindAddress.getAddress()
							.getHostAddress(), bindAddress.getPort(),
					transaction.getHops() + 1, 0, block, false);
			connection.send(resultValidation);
		}
	}

	public void handleResultValidation(InetSocketAddress localAddress,
			Connection connection, ResultValidation resultValidation) {
		if(!resultValidation.isResult()){
			BlockChain.getInstance().removeBlock(resultValidation.getBlock());
		}
		
		
	}
	
    /**
     * Initiates a Add Block operation in the network. If there is an ongoing Add Block operation, it only attaches to it without
     * starting a new Add Block.
     *
     * @param future future to be informed once the Ping is completed.
     */
	public void executeTransaction(CompletableFuture<Collection<String>> future) {
		/* TransactionContext transactionContext = currentTransaction.get(config.getPeerName());
	        if (transactionContext == null) {
	            transactionContext = discoveryTransaction();
	        } else {
	            LOGGER.info("Attaching to the already existing ping context");
	        }

	        if (future != null) {
	            transactionContext.addFuture(future);
	        }*/
		LOGGER.info("Initing send message");
		Collection<Connection> connections = connectionService.getConnections();
		for (Iterator iterator = connections.iterator(); iterator.hasNext();) {
			Connection connection = (Connection) iterator.next();
			connection.send(new Transaction(this.config.getPeerName(), 1, 1, 5));
			LOGGER.info("Peer: {}",connection.getPeerName());
		}
		LOGGER.info("Finished send message");
	}
	
	private TransactionContext discoveryTransaction() {
        final int ttl = config.getAddBlockTTL();

        LOGGER.info("Doing a full ping with ttl={}", ttl);

        final Transaction transaction = new Transaction(config.getPeerName(), ttl, 0, config.getTransactionTimeoutMillis());
        transaction.setTransactionStartTimestamp(System.currentTimeMillis());
        final TransactionContext transactionContext = new  TransactionContext(transaction, null);
        currentTransaction.put(config.getPeerName(), transactionContext);

        for (Connection connection : connectionService.getConnections()) {
            connection.send(transaction);
        }

        return transactionContext;
    }


	
}
