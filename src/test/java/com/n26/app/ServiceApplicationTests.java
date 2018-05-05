package com.n26.app;

import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.n26.app.controller.StatisticsController;
import com.n26.app.model.RecordTransactionRequest;
import com.n26.app.model.TransactionStatisticResponse;

/********************************************************
 * Test bed used for testing our service. These test cases will be executed during a maven install.
 * 
 * @author pgobin
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
// This annotation allow our test cases to run be method naming alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceApplicationTests {

	// Inject the API Controller
	@Autowired
	private StatisticsController _statisticsController;

	/************************************************************
	 * Helper method to get a dummy HTTP Header to use for invoking our rest endpoints.
	 * 
	 * @return
	 *************************************************************/
	private HttpHeaders getHttpHeaders()
	{
		HttpHeaders dummyHeaders = new HttpHeaders();
		dummyHeaders.add("x-account", "n26");
		dummyHeaders.add("x-authtoken", "0505156e-bda9-41a3-88e3-29e62b643dd9");
		return dummyHeaders;
	}

	/************************************************************
	 * Usecase: <br/>
	 * Request statistics on an empty record set.
	 * 
	 * Result:<br/>
	 * Returns a 204 (no-content).
	 */
	@Test
	public void aStatistics_noContentAvailable()
	{
		HttpHeaders dummyHeaders = getHttpHeaders();
		ResponseEntity<TransactionStatisticResponse> result = _statisticsController.statistics(dummyHeaders);
		assertTrue(result.getStatusCode() == HttpStatus.NO_CONTENT);
	}

	/************************************************************
	 * Usecase: <br/>
	 * Test adding a transaction that is older that 60 seconds.
	 * 
	 * Result:<br/>
	 * HttpResponse should produce an Http Status of 204 (no-content)
	 */
	@Test
	public void bAddTransactionOlderThan60Seconds()
	{
		HttpHeaders dummyHeaders = getHttpHeaders();
		long timestamp = Instant.now().minusSeconds(80).toEpochMilli();
		ResponseEntity<?> resp = _statisticsController.transactions(dummyHeaders, new RecordTransactionRequest(12, timestamp));
		assertTrue(HttpStatus.NO_CONTENT == resp.getStatusCode());
	}

	/************************************************************
	 * Usecase: <br/>
	 * Test adding transactions that are valid. Valid transactions are transactions that are within 60 seconds.
	 * 
	 * Result:<br/>
	 * Returns a json statistics payload object. If no statistics data is available, a 204 (no-content) is returned.
	 */
	@Test
	public void cStatistics()
	{

		HttpHeaders dummyHeaders = getHttpHeaders();
		long timestamp = Instant.now().toEpochMilli();
		_statisticsController.transactions(dummyHeaders, new RecordTransactionRequest(12, timestamp));
		_statisticsController.transactions(dummyHeaders, new RecordTransactionRequest(13, timestamp));
		_statisticsController.transactions(dummyHeaders, new RecordTransactionRequest(22.50, timestamp));
		_statisticsController.transactions(dummyHeaders, new RecordTransactionRequest(12.30, timestamp));

		TransactionStatisticResponse result = _statisticsController.statistics(dummyHeaders).getBody();
		assertTrue(result.getCount() == 4);
		assertTrue(result.getSum() == 59.8);
		assertTrue(result.getAvg() == 14.95);
		assertTrue(result.getMin() == 12);
		assertTrue(result.getMax() == 22.50);
	}

}
