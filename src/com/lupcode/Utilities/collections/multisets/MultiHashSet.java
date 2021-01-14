package com.lupcode.Utilities.collections.multisets;

import java.util.HashMap;

/**
 * {@link MultiSet} that uses a hash structure for storage.
 * This {@link MultiHashSet} is thread-safe
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-14
 * @param <E> Element that should be hold
 */
public class MultiHashSet<E> extends MultiSet<E> implements Cloneable {
	
	/**
	 * Creates a {@link MultiHashSet}
	 */
	public MultiHashSet() {
		super(new HashMap<>());
	}
	
	/**
	 * Creates a {@link MultiHashSet}
	 * @param initialCapacity The initial capacity
	 */
	public MultiHashSet(int initialCapacity) {
		super(new HashMap<>(initialCapacity));
	}

	/**
	 * Creates a {@link MultiHashSet}
	 * @param initialCapacity The initial capacity
	 * @param loadFactor The load factor
	 */
	public MultiHashSet(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor));
	}
	
	@Override
	protected MultiHashSet<E> clone() {
		lock.readLock().lock();
		try {
			MultiHashSet<E> clone = new MultiHashSet<>(size);
			clone.elements.putAll(elements);
			return clone;
		} finally {
			lock.readLock().unlock();
		}
	}
}
