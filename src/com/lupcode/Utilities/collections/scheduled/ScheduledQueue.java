package com.lupcode.Utilities.collections.scheduled;

import java.util.Collection;
import java.util.Queue;

/**
 * Extends the {@link Queue} interface to specific scheduled queues 
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-07
 * @param <E> Element that queue should hold
 */
public interface ScheduledQueue<E> extends Queue<E> {

	/**
	 * Returns the total size of elements contained in this 
	 * queue including scheduled elements
	 * @return Amount of available and scheduled elements
	 */
	public long getTotalSize();
	
	/**
	 * Returns only true if this queue really has no more elements 
	 * (also no scheduled elements)
	 * @return True if no elements (also not scheduled ones)
	 */
	public boolean isCompletelyEmpty();
	
	/**
	 * Adds the given element to the collection so that it becomes 
	 * available after the given duration or throwing an {@code IllegalStateException}
     * if no space is currently available.
     * @param duration Milliseconds from now after which element should become available
	 * @param e Element that should be added
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public boolean addIn(long duration, E e);
	
	/**
	 * Adds the given element to the collection so that it becomes 
	 * available at the given system time or throwing an {@code IllegalStateException}
     * if no space is currently available.
	 * @param time Time at which the new element should become available
	 * @param e Element that should be added
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public boolean addAt(long time, E e);
	
	/**
	 * Adds all elements from collection so that they become 
	 * available after the given duration 
	 * @param duration Milliseconds from now after which elements should become available
	 * @param c Collection of elements that should be added
	 * @return True if this collection changed as a result of the call
	 */
	public boolean addAllIn(long duration, Collection<? extends E> c);
	
	/**
	 * Adds all elements from collection so that they become 
	 * available at the given system time
	 * @param time Time at which new elements should become available
	 * @param c Collection of elements that should be added
	 * @return True if this collection changed as a result of the call
	 */
	public boolean addAllAt(long time, Collection<? extends E> c);
	
	/**
	 * Adds the given element to the collection so that it becomes 
	 * available after the given duration. 
	 * When using a capacity-restricted queue, this method is generally
     * preferable to {@link #add}, which can fail to insert an element only
     * by throwing an exception.
	 * @param duration Milliseconds from now after which element should become available
	 * @param e Element that should be added
	 * @return {@code true} if the element was added to this queue, else
     *         {@code false}
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public boolean offerIn(long duration, E e);
	
	/**
	 * Adds the given element to the collection so that it becomes 
	 * available at the given system time.
	 * When using a capacity-restricted queue, this method is generally
     * preferable to {@link #add}, which can fail to insert an element only
     * by throwing an exception.
	 * @param time Time at which the new element should become available
	 * @param e Element that should be added
	 * @return {@code true} if the element was added to this queue, else
     *         {@code false}
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
	 */
	public boolean offerAt(long time, E e);
}
