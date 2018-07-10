package com.basrikahveci.p2p.peer.service;

import java.net.InetSocketAddress;

import org.mockito.cglib.core.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basrikahveci.p2p.blockchain.BlockChain;
import com.basrikahveci.p2p.peer.Config;
import com.basrikahveci.p2p.peer.network.Connection;
import com.basrikahveci.p2p.peer.network.message.RequestValidation;
import com.basrikahveci.p2p.peer.network.message.ResultValidation;
import com.basrikahveci.p2p.peer.network.message.ping.Ping;
import com.basrikahveci.p2p.peer.network.message.ping.Pong;

public class BlockChainService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BlockChainService.class);
	private final Config config;

	private final ConnectionService connectionService;

	public BlockChainService(ConnectionService connectionService, Config config) {
		this.connectionService = connectionService;
		this.config = config;
	}

	/**
	 * Handles a Block operation initiated by another node. If the received
	 * {@link Block} message is allowed to be propagated, this peer also
	 * propagates it to its own neighbours. Additionally, it sends a
	 * {@link Pong} message back to the neighbour that has sent the {@link Ping}
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
	public void handleRequestValidation(InetSocketAddress bindAddress,
			Connection connection, RequestValidation requestValidation) {

		final String requestValidationPeerName = requestValidation
				.getPeerName();

		if (requestValidationPeerName.equals(connection.getPeerName())) {
			LOGGER.info("Handling {} of initiator {} with ttl={}",
					requestValidation, requestValidationPeerName,
					requestValidation.getTtl());
		} else {
			LOGGER.info(
					"Handling {} of initiator {} and forwarder {} with ttl={} and hops={}",
					requestValidation, requestValidationPeerName,
					connection.getPeerName(), requestValidation.getTtl(),
					requestValidation.getHops());
		}
		com.basrikahveci.p2p.blockchain.Block block = requestValidation.getBlock();
		BlockChain.getInstance().addBlock(block);
		if (BlockChain.getInstance().isChainValid()) {

			requestValidation
					.setBlockStartTimestamp(System.currentTimeMillis());

			final ResultValidation resultValidation = new ResultValidation(
					requestValidationPeerName, config.getPeerName(),
					config.getPeerName(), bindAddress.getAddress()
							.getHostAddress(), bindAddress.getPort(),
					requestValidation.getHops() + 1, 0,block, true);
			connection.send(resultValidation);
			final RequestValidation next = requestValidation.next();
			if (next != null) {
				for (Connection neighbour : connectionService.getConnections()) {
					if (!neighbour.equals(connection)
							&& !neighbour.getPeerName().equals(
									requestValidation.getPeerName())) {
						LOGGER.info("Forwarding {} to {} for initiator {}",
								requestValidation, neighbour.getPeerName(),
								requestValidation.getPeerName());
						neighbour.send(requestValidation);
					}
				}
			}
		}else{
			final ResultValidation resultValidation = new ResultValidation(
					requestValidationPeerName, config.getPeerName(),
					config.getPeerName(), bindAddress.getAddress()
							.getHostAddress(), bindAddress.getPort(),
					requestValidation.getHops() + 1, 0, block, false);
			connection.send(resultValidation);
		}
	}

	public void handleResultValidation(InetSocketAddress localAddress,
			Connection connection, ResultValidation resultValidation) {
		if(!resultValidation.isResult()){
			BlockChain.getInstance().removeBlock(resultValidation.getBlock());
		}
		
		
	}
}
