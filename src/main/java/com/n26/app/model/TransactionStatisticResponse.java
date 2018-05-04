package com.n26.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

/*******************************************************************
 * This is a simple DTO which allow us to operate using POJOs for json.
 * 
 * This POJO used when requesting statistics via the get/statistics.<br/>
 * 
 * By annotating the attributes with API documentation, it allow you to understand the request when viewed in swagger ui.
 * 
 * @author pgobin
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatisticResponse {

	@ApiModelProperty(value = "The total sum of transaction value in the last 60 seconds.", required = true, example = "1000", allowEmptyValue = false)
	private double sum;
	@ApiModelProperty(value = "The average amount of transaction value in the last 60 seconds.", required = true, example = "100", allowEmptyValue = false)
	private double avg;
	@ApiModelProperty(value = "The single highest transaction value in the last 60 seconds.", required = true, example = "200", allowEmptyValue = false)
	private double max;
	@ApiModelProperty(value = "The single lowest transaction value in the last 60 seconds.", required = true, example = "50", allowEmptyValue = false)
	private double min;
	@ApiModelProperty(value = "The total number of transaction happened in the last 60 seconds.", required = true, example = "10", allowEmptyValue = false)
	private long count;

	public TransactionStatisticResponse()
	{

	}

	/**
	 * @return the sum
	 */
	public double getSum()
	{
		return sum;
	}

	/**
	 * @param sum
	 *            the sum to set
	 */
	public void setSum(double sum)
	{
		this.sum = sum;
	}

	/**
	 * @return the avg
	 */
	public double getAvg()
	{
		return avg;
	}

	/**
	 * @param avg
	 *            the avg to set
	 */
	public void setAvg(double avg)
	{
		this.avg = avg;
	}

	/**
	 * @return the max
	 */
	public double getMax()
	{
		return max;
	}

	/**
	 * @param max
	 *            the max to set
	 */
	public void setMax(double max)
	{
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public double getMin()
	{
		return min;
	}

	/**
	 * @param min
	 *            the min to set
	 */
	public void setMin(double min)
	{
		this.min = min;
	}

	/**
	 * @return the count
	 */
	public long getCount()
	{
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(long count)
	{
		this.count = count;
	}

}
