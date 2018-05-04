package com.n26.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.app.bo.TransactionStatisticsManager;
import com.n26.app.model.RecordTransactionRequest;
import com.n26.app.model.TransactionStatisticResponse;

import io.swagger.annotations.ApiOperation;

/*******************************************************************
 * This is our Statistics Controller, it exposes two endpoints with access to our StatisticsManager. All business logic are in the StaticticsManager, hence removing any coupling
 * and encourages code/logic isolation.
 * 
 * @author pgobin
 *
 */
@RestController
@RequestMapping("/v1/StatisticsController")
public class StatisticsController {

	private static final Logger log = LogManager.getLogger(StatisticsController.class);
	@Autowired
	private TransactionStatisticsManager _transactionStatisticsManager;

	public StatisticsController()
	{

	}

	/*************************************************************
	 * 
	 * @param headers
	 * @param recordTransactionRequest
	 * @return
	 * @throws Exception
	 **************************************************************/
	@RequestMapping(method = RequestMethod.POST, produces = "application/json", value = "/transactions")
	@ApiOperation(value = "Transaction tracker.", notes = "Invoke this endpoint to record all transactions. Returns HTTP status code 201 (created) if the transaction was recorded successfully."
		+ "<br/>Return HTTP status code 204 (No Content) if the transaction you are trying to record is older that 60 seconds.")
	public ResponseEntity<?> transactions(@RequestHeader HttpHeaders headers, @RequestBody(required = true) RecordTransactionRequest recordTransactionRequest)
	{
		log.debug("Request to record transaction");
		boolean recorded = _transactionStatisticsManager.recordTransaction(recordTransactionRequest);
		return ResponseEntity.status(recorded ? HttpStatus.CREATED : HttpStatus.NO_CONTENT).build();
	}

	/*************************************************************
	 * 
	 * @param headers
	 * @return
	 **************************************************************/
	@ApiOperation(value = "Returns statistics.", notes = "This endpoind returns statistics based on the transactions which happened it the last 60 seconds.<br/>"
		+ "If there are no transactions, an HTTP status code of 204 (no content) is returned.")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "/statistics")
	public ResponseEntity<TransactionStatisticResponse> statistics(@RequestHeader HttpHeaders headers)
	{
		log.debug("Request for transaction statistics..");
		TransactionStatisticResponse response = _transactionStatisticsManager.getTransactionStatistics();
		if (response == null)
		{
			log.warn("There are no transactions currently recorded");
			return ResponseEntity.noContent().build();
		}
		log.debug("Returning transactions to client.");
		return new ResponseEntity<TransactionStatisticResponse>(response, HttpStatus.OK);
	}

}
