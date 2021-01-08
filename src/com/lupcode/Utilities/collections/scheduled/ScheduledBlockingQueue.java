package com.lupcode.Utilities.collections.scheduled;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
     * @param duration Milliseconds from now after which element should become available
	 * @param e Element that should be added
	 * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public void putIn(long duration, E e) throws InterruptedException;
	
	/**
	 * Adds the given element to the collection so that it becomes 
	 * available at the given system time, waiting if necessary
     * for space to become available.
     * @param time Time at which the new element should become available
	 * @param e Element that should be added
	 * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public void putAt(long time, E e) throws InterruptedException;
	
	/**
     * Adds the given element to the collection so that it becomes 
	 * available after the given duration, waiting if necessary
     * for space to become available for a certain time.
     * @param duration Milliseconds from now after which element should become available
     * @param e Element that should be added
     * @param timeout How long to wait before giving up, in units of
     *        {@code unit}
     * @param unit {@code TimeUnit} determining how to interpret the
     *        {@code timeout} parameter
     * @return {@code true} if successful, or {@code false} if
     *         the specified waiting time elapses before space is available
     * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    public boolean offerIn(long duration, E e, long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
     * Adds the given element to the collection so that it becomes 
	 * available at the given system time, waiting if necessary
     * for space to become available for a certain time.
     * @param time Time at which the new element should become available
     * @param e Element that should be added
     * @param timeout How long to wait before giving up, in units of
     *        {@code unit}
     * @param unit {@code TimeUnit} determining how to interpret the
     *        {@code timeout} parameter
     * @return {@code true} if successful, or {@code false} if
     *         the specified waiting time elapses before space is available
     * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    public boolean offerAt(long time, E e, long timeout, TimeUnit unit) throws InterruptedException;
}
