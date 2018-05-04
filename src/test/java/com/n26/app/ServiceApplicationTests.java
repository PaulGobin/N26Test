package com.n26.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.n26.app.controller.StatisticsController;
import com.n26.app.model.RecordTransactionRequest;
import com.n26.app.model.TransactionStatisticResponse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceApplicationTests {
	
	@Autowired
	private StatisticsController _statisticsController;

	private HttpHeaders getHttpHeaders() {
		HttpHeaders dummyHeaders = new HttpHeaders();
		dummyHeaders.add("x-account", "n26");
		dummyHeaders.add("x-authtoken", "0505156e-bda9-41a3-88e3-29e62b643dd9");
		return dummyHeaders;
	}
	
	@Test
	public void addTransactionOlderThan60Seconds()
	{		
		HttpHeaders dummyHeaders = getHttpHeaders();
		long timestamp = Instant.now().minusSeconds(80).toEpochMilli();
		ResponseEntity<?> resp = _statisticsController.transactions(dummyHeaders, new RecordTransactionRequest(12, timestamp));
		assertTrue(HttpStatus.NO_CONTENT ==  resp.getStatusCode());
	}
	
	
	@Test
	public void statistics()
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
