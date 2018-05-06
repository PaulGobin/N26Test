package com.n26.app.bo;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.n26.app.model.RecordTransactionRequest;
import com.n26.app.model.TransactionStatisticResponse;

/***************************************************
 * This is the main worker class, it manages, maintains and validate the transactions. It is using a Concurrent Set for storing transactions. <br>
 * You can easily swap out the Set for persistent storage.
 * 
 * When a transaction is to be saved, it validates that the transaction is not older than 60 seconds epoch UTC time.<br>
 * Additionally, it automatically evict transactions that are in the Set which are older that 60 seconds via a background scheduler<br/>
 * , this ensure that only valid and non-expired transactions are kept in memory,<br/>
 * it also maintains the statistics and keep it current so we can achieve a <br/>
 * constant time (O)(1) by not having to process all the entries in the list what a get statistics is requested.
 * 
 * @author pgobin
 *
 */
@EnableScheduling
@Service
public class TransactionStatisticsManager {

	private static final Logger log = LogManager.getLogger(TransactionStatisticsManager.class);

	// Defines the validity of a transaction, transactions within this time is valid
	private static final int _validTransactionTimeInSeconds = 60;

	// Create a concurrent, thread safe, high performance set used for storing transactions
	private static final Set<RecordTransactionRequest> _transactions = ConcurrentHashMap.newKeySet(100);

	// Use a single TransactionStatistic object for keeping track of the statistics.
	// This allow us to maintain a single recorder which provides a constant time per (O(1)) requirement
	private static final TransactionStatisticResponse _runningStatistics = new TransactionStatisticResponse();

	/************************************************
	 * Record a transaction in the _transactions concurrent set. <br>
	 * A transaction is valid only if the timestamp in {@link RecordTransactionRequest} is <b>NOT</b> older than 60 seconds epoch time.<br>
	 * If the epoch timestamp is older that 60 seconds, this transaction is discarded.
	 * 
	 * Additionally, updateTransactionStatisticsForO1() with the current statistics.
	 * 
	 * @param transactionRequest
	 * @return
	 */
	public boolean recordTransaction(RecordTransactionRequest transactionRequest)
	{
		try
		{
			if (transactionRequest == null)
			{
				log.error("Cannot record transaction. The transaction to recorde is null");
				return false;
			}
			// verify that the transaction timestamp is not in the future
			long now = Instant.now().toEpochMilli();
			if (transactionRequest.getTimestamp() > now)
			{
				log.error("Transaction ignored, you cannot add a transaction that is in the future.");
				return false;
			}
			// get the timestamp of now minus 60 seconds.
			Instant instant = Instant.now().minusSeconds(_validTransactionTimeInSeconds);
			long timeStampMillis60SecondsInthePast = instant.toEpochMilli();
			// verify that the transaction is withing 60 seconds
			if (transactionRequest.getTimestamp() >= timeStampMillis60SecondsInthePast)
			{
				_transactions.add(transactionRequest);
				updateTransactionStatisticsForO1();
				return true;
			}
			log.warn("Cannot record transaction because the transaction timestamp [" + transactionRequest.getTimestamp() + "] is older than 60 seconds." + System.lineSeparator() + "Current epoch is "
				+ Instant.now().toEpochMilli());
			return false;
		} catch (Exception ex)
		{
			log.error("An error occurred trying to add transaction " + transactionRequest.toString());
			return false;
		}
	}

	/******************************************************
	 * Update the singleton _runningStatistics object to achieve (O(1).<br>
	 * This allows the getTransactionStatistics() method to return the single object without having to perform calculations<br>
	 * on the list of transactions when requested.
	 */
	private void updateTransactionStatisticsForO1()
	{
		if (_transactions.isEmpty())
		{
			return;
		}
		// synchronized block around the _runningStatistics object to avoid deadlock while we update the statistics.
		synchronized (_runningStatistics)
		{
			long timeStampMillis60SecondsInthePast = Instant.now().minusSeconds(_validTransactionTimeInSeconds).toEpochMilli();
			// remove transactions that are older than 60 seconds from the _transactions repository to conserver resources
			// and to keep only valid transactions.
			_transactions.removeIf(x -> x.getTimestamp() < timeStampMillis60SecondsInthePast);
			DoubleSummaryStatistics stats = _transactions.stream().mapToDouble((x) -> x.getAmount()).summaryStatistics();
			log.info(stats);
			_runningStatistics.setAvg(stats.getAverage());
			_runningStatistics.setCount(stats.getCount());
			_runningStatistics.setMin(stats.getMin());
			_runningStatistics.setMax(stats.getMax());
			_runningStatistics.setSum(stats.getSum());
			if (stats.getCount() < 1)
			{
				log.info("All transactions expired!");
			}
		}
	}

	/*****************************************************
	 * 
	 * Demonstrate (O(1))- Constant time regardless of the number of data.<br>
	 * Using a singleton object when possible provides the most optimal performance and time constant,<br>
	 * since we only need to track a singleton vs using a Set and pulling from an index, generally 0 if there are many.<br>
	 * 
	 * (O)(1) Set.get(0);
	 * 
	 * We could have easily call updateTransactionStaticticsForO1 in this method, <br>
	 * to do realtime calculations on all the non-expired objects in __transactions, <br>
	 * but doing so will result in (O(n) condition vs (O(1)
	 * 
	 * @return
	 */
	public TransactionStatisticResponse getTransactionStatistics()
	{
		if (_transactions.isEmpty())
		{
			return null;
		}
		return _runningStatistics;
	}

	/****************************************************
	 * How do we evict expired transactions from the _transaction set?<br>
	 * This cleanup scheduler allows us to remove expired transactions and only keep transactions that are 60 seconds or less.<br>
	 * It also keeps the statistics report current and allows the getTransactionStatistics()<br/>
	 * method to quickly return, in a time constant manner, the transaction statistics.
	 */
	@Scheduled(fixedRate = 1000, initialDelay = 5000)
	private void maintainStatisticsForO1()
	{
		updateTransactionStatisticsForO1();
	}
}
