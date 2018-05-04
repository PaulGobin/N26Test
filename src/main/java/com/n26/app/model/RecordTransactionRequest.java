package com.n26.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTransactionRequest {

	@ApiModelProperty(value = "Transaction amount", required = true, example = "21.3", allowEmptyValue = false)
	private double amount;
	@ApiModelProperty(value = "Transaction timestamp in epoch in mills in UTC timezone (this is not the current timestamp)", required = true, example = "1478192204000", allowEmptyValue = false)
	private long timestamp;

	public RecordTransactionRequest()
	{

	}

	/**
	 * @param amount
	 * @param timestamp
	 */
	public RecordTransactionRequest(double amount, long timestamp)
	{
		super();
		this.amount = amount;
		this.timestamp = timestamp;
	}

	/**
	 * @return the amount
	 */
	public double getAmount()
	{
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(double amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

}
