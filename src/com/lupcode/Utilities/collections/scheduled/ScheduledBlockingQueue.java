package com.lupcode.Utilities.collections.scheduled;

import java.util.concurrent.BlockingQueue;

/**
 * Extends the {@link ScheduledQueue} interface to specific scheduled blocking queues 
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-07
 * @param <E> Element that queue should hold
 */
public interface ScheduledBlockingQueue<E> extends ScheduledQueue<E>, BlockingQueue<E> {

	/**
	 * Adds the given element to the collection so that it becomes 
	 * available after the given duration, waiting if necessary
     * for space to become available.
	 * @param e Element that should be added
	 * @param duration Milliseconds from now after which element should become available
	 * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public void putIn(E e, long duration) throws InterruptedException;
	
	/**
	 * Adds the given element to the collection so that it becomes 
	 * available at the given system time, waiting if necessary
     * for space to become available.
	 * @param e Element that should be added
	 * @param duration Milliseconds from now after which element should become available
	 * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public void putAt(E e, long time) throws InterruptedException;
	
}
