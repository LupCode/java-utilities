package com.lupcode.Utilities.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * {@link HashSet} but allows concurrent access
 * @author LupCode.com (Luca Vogels)
 * @since 2021-02-03
 * @param <E> Element that should be stored
 */
public class ConcurrentHashSet<E> extends HashSet<E> {
	private static final long serialVersionUID = 1L;

	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ConcurrentHashSet() {
		super();
	}
	
	public ConcurrentHashSet(int initialCapacity) {
		super(initialCapacity);
	}
	
	public ConcurrentHashSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}
	
	public ConcurrentHashSet(Collection<E> c) {
		super();
		addAll(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		lock.writeLock().lock();
		try {
			return super.addAll(c);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		lock.readLock().lock();
		try {
			return super.containsAll(c);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		lock.readLock().lock();
		try {
			return super.equals(o);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		lock.writeLock().lock();
		try {
			return super.removeAll(c);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		lock.writeLock().lock();
		try {
			return super.removeAll(c);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public Object[] toArray() {
		lock.readLock().lock();
		try {
			return super.toArray();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		lock.readLock().lock();
		try {
			return super.toArray(a);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean add(E e) {
		lock.writeLock().lock();
		try {
			return super.add(e);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void clear() {
		lock.writeLock().lock();
		try {
			super.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public Object clone() {
		lock.readLock().lock();
		try {
			return new ConcurrentHashSet<>(this);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean contains(Object o) {
		lock.readLock().lock();
		try {
			return super.contains(o);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void forEach(Consumer<? super E> action) {
		lock.writeLock().lock();
		try {
			super.forEach(action);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		try {
			return super.isEmpty();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Iterator<E> iterator() {
		lock.readLock().lock();
		try {
			return super.iterator();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean remove(Object o) {
		lock.writeLock().lock();
		try {
			return super.remove(o);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		lock.writeLock().lock();
		try {
			return super.removeIf(filter);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();
		} finally {
			lock.readLock().unlock();
		}
	}
	
}
