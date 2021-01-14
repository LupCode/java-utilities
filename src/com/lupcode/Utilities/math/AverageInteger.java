package com.lupcode.Utilities.math;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Takes in {@link Integer}s and lazily calculates the average 
 * on a given amount of the latest added numbers
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-14
 */
public class AverageInteger extends Number {
	private static final long serialVersionUID = 1L;
	
	protected int capacity;
	protected Queue<Integer> queue;
	protected double average = 0;
	protected boolean needAverageCalc = false;
	
	/**
	 * Creates an {@link AverageInteger} that calculates 
	 * the average on a given amount of received {@link Integer}s
	 * @param capacity Amount of numbers that should be hold for calculation
	 */
	public AverageInteger(int capacity) {
		queue = new LinkedList<>();
		setCapacity(capacity);
	}
	
	protected void checkAverage() {
		if(!needAverageCalc) return;
		this.average = 0;
		double size = queue.size();
		for(int i : queue) this.average += i / size;
		this.needAverageCalc = false;
	}
	
	/**
	 * Returns the size 
	 * @return How many elements are currently hold
	 */
	public int size() {
		return queue.size();
	}
	
	/**
	 * Removes all currently hold elements 
	 * so the calculated value gets forgotten
	 */
	public synchronized void clear() {
		this.queue.clear();
		this.average = 0;
		this.needAverageCalc = false;
	}
	
	/**
	 * Returns the capacity
	 * @return How many elements can be hold for calculation
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Sets the capacity how many elements can be hold 
	 * for the calculation
	 * @param capacity Amount of elements that can be hold
	 * @throws IllegalArgumentException if capacity is smaller than 1
	 */
	public synchronized void setCapacity(int capacity) throws IllegalArgumentException {
		if(capacity <= 0) throw new IllegalArgumentException("Capacity cannot be smaller than 1");
		this.capacity = capacity;
		if(queue.size() > capacity) {
			this.needAverageCalc = true;
			do queue.remove(); while(queue.size() > capacity);
		}
	}
	
	/**
	 * Adds the new value and re-calculates
	 * @param value Value that should be added
	 */
	public synchronized void add(int value) {
		double size = queue.size();
		if(queue.size() == capacity) {
			checkAverage();
			int rem = queue.remove();
			queue.add(value);
			this.average += (value-rem) / size;
		} else {
			queue.add(value);
			this.needAverageCalc = true;
		}
	}
	
	/**
	 * Returns the calculated average
	 * @return Average value
	 */
	public double getAverage() {
		checkAverage();
		return average;
	}
	
	/**
	 * Returns the calculated average
	 * @return Average value
	 */
	@Override
	public int intValue() {
		checkAverage();
		return (int) average;
	}

	/**
	 * Returns the calculated average
	 * @return Average value
	 */
	@Override
	public long longValue() {
		checkAverage();
		return (long) average;
	}

	/**
	 * Returns the calculated average
	 * @return Average value
	 */
	@Override
	public float floatValue() {
		checkAverage();
		return (float) average;
	}

	/**
	 * Returns the calculated average
	 * @return Average value
	 */
	@Override
	public double doubleValue() {
		checkAverage();
		return average;
	}

	@Override
	public String toString() {
		checkAverage();
		return new StringBuilder(getClass().getSimpleName()).append("{average=").append(average).
				append("}").toString();
	}
}
