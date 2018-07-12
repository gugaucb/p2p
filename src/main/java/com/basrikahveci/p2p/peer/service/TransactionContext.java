package com.basrikahveci.p2p.peer.service;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basrikahveci.p2p.blockchain.Transaction;
import com.basrikahveci.p2p.peer.network.Connection;
import com.basrikahveci.p2p.peer.network.message.ResultValidation;
import com.basrikahveci.p2p.peer.network.message.ping.Pong;

/**
 * Maintains all information related to an ongoing Ping operation.
 */
public class TransactionContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionContext.class);

    private final Transaction transaction;

    private final Connection connection;

    // peer name -> pong
    private final Map<String, ResultValidation> resultValidations = new HashMap<>();

    private final List<CompletableFuture<Collection<String>>> futures = new ArrayList<>();


    public TransactionContext(Transaction transaction, Connection connection) {
    	 this.transaction = transaction;
         this.connection = connection;
	}

	public String getPeerName() {
        return transaction.getPeerName();
    }

    public Transaction getPing() {
        return transaction;
    }

    public Connection getConnection() {
        return connection;
    }

    public Collection<ResultValidation> getPongs() {
        return Collections.unmodifiableCollection(resultValidations.values());
    }

    public boolean handleResultValidation(final String thisServerName, final ResultValidation resultValidation) {
        final String resultValidationServerName = resultValidation.getPeerName();
        if (resultValidations.containsKey(resultValidationServerName)) {
            LOGGER.debug("{} from {} is already handled for {}", resultValidation, resultValidationServerName, transaction.getPeerName());
            return false;
        }

        resultValidations.put(resultValidationServerName, resultValidation);

        LOGGER.debug("Handling {} from {} for {}. Pong #: {}", resultValidation, resultValidationServerName, transaction.getPeerName(), resultValidations.size());

        if (!thisServerName.equals(transaction.getPeerName())) {
            if (connection != null) {
                final ResultValidation next = resultValidation.next(thisServerName);
                if (next != null) {
                    LOGGER.debug("Forwarding {} to {} for initiator {}", resultValidation, connection.getPeerName(), transaction.getPeerName());
                    connection.send(next);
                } else {
                    LOGGER.error("Invalid {} received from {} for {}", resultValidation, resultValidationServerName, transaction.getPeerName());
                }
            } else {
                LOGGER.error("No connection is found in ping context for {} from {} for {}", resultValidation, resultValidationServerName,
                        transaction.getPeerName());
            }
        }

        return true;
    }

    public void addFuture(CompletableFuture<Collection<String>> future) {
        futures.add(future);
    }

    public boolean isTimeout() {
        return transaction.getTransactionStartTimestamp() + transaction.getTransactionTimeoutDurationInMillis() <= System.currentTimeMillis();
    }

    public List<CompletableFuture<Collection<String>>> getFutures() {
        return unmodifiableList(futures);
    }

    public boolean removeResultValidation(final String serverName) {
        return resultValidations.remove(serverName) != null;
    }

    @Override
    public String toString() {
        return "ResultValidationContext{" +
                "resultValidations=" + resultValidations +
                ", connection=" + connection +
                ", transaction=" + transaction +
                '}';
    }

}
