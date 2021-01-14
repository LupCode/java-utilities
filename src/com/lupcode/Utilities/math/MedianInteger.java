package com.lupcode.Utilities.math;

import java.util.ArrayList;

/**
 * Takes in {@link Integer}s and lazily calculates the median 
 * as well as the average on a given amount of the latest added numbers
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-14
 */
public class MedianInteger extends AverageInteger {
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<Integer> sortedList = null;
	protected int median = 0;
	protected boolean needMedianCalc = false;
	
	
	/**
	 * Creates an {@link MedianInteger} that calculates 
	 * the median as well as the average on a given amount of received {@link Integer}s
	 * @param capacity Amount of numbers that should be hold for calculation
	 */
	public MedianInteger(int capacity) {
		super(capacity);
		this.sortedList = new ArrayList<>(capacity);
	}
	
	protected void checkMedian() {
		if(!needMedianCalc) return;
		this.sortedList.sort(null);
		int size = this.sortedList.size();
		this.median = size>0 ? this.sortedList.get(size / 2) : 0;
		this.needMedianCalc = false;
	}
	
	@Override
	public synchronized void clear() {
		super.clear();
		this.sortedList.clear();
		this.median = 0;
		this.needMedianCalc = false;
	}
	
	@Override
	public synchronized void setCapacity(int capacity) throws IllegalArgumentException {
		super.setCapacity(capacity);
		if(this.sortedList != null && this.sortedList.size() > capacity) {
			this.sortedList.clear();
			this.sortedList.addAll(this.queue);
			this.needMedianCalc = true;
		}
	}
	
	@Override
	public synchronized void add(int value) {
		if(this.sortedList.size() >= capacity)
			this.sortedList.remove(this.queue.peek());
		this.sortedList.add(value);
		this.needMedianCalc = true;
		super.add(value);
	}
	
	/**
	 * Returns the calculated median
	 * @return Median value
	 */
	public int getMedian() {
		checkMedian();
		return median;
	}
	
	/**
	 * Returns the calculated median
	 * @return Median value
	 */
	@Override
	public int intValue() {
		checkMedian();
		return median;
	}
	
	/**
	 * Returns the calculated median
	 * @return Median value
	 */
	@Override
	public long longValue() {
		checkMedian();
		return median;
	}
	
	/**
	 * Returns the calculated median
	 * @return Median value
	 */
	@Override
	public float floatValue() {
		checkMedian();
		return median;
	}
	
	/**
	 * Returns the calculated median
	 * @return Median value
	 */
	@Override
	public double doubleValue() {
		checkMedian();
		return median;
	}
	
	@Override
	public String toString() {
		checkAverage();
		checkMedian();
		return new StringBuilder(getClass().getSimpleName()).append("{median=").append(median).
				append("; average=").append(average).append("}").toString();
	}
}
