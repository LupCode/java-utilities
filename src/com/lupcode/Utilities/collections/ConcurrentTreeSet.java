package com.lupcode.Utilities.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * {@link TreeSet} but allows concurrent access
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 * @param <E> Element that should be stored
 */
public class ConcurrentTreeSet<E> extends TreeSet<E> {
	private static final long serialVersionUID = 1L;
	
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ConcurrentTreeSet() {
		super();
	}
	public ConcurrentTreeSet(Collection<? extends E> c) {
		super();
		addAll(c);
	}
	public ConcurrentTreeSet(Comparator<? super E> comparator) {
		super(comparator);
	}
	public ConcurrentTreeSet(SortedSet<E> s) {
		super(s.comparator());
		addAll(s);
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
			return super.retainAll(c);
		} finally {
			lock.writeLock().unlock();
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
	public boolean addAll(Collection<? extends E> c) {
		lock.writeLock().lock();
		try {
			return super.addAll(c);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public E ceiling(E e) {
		lock.readLock().lock();
		try {
			return super.ceiling(e);
		} finally {
			lock.readLock().unlock();
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
	public boolean contains(Object o) {
		lock.readLock().lock();
		try {
			return super.contains(o);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Iterator<E> descendingIterator() {
		lock.readLock().lock();
		try {
			return super.descendingIterator();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public NavigableSet<E> descendingSet() {
		lock.readLock().lock();
		try {
			return super.descendingSet();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E first() {
		lock.readLock().lock();
		try {
			return super.first();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E floor(E e) {
		lock.readLock().lock();
		try {
			return super.floor(e);
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
	public SortedSet<E> headSet(E toElement) {
		lock.readLock().lock();
		try {
			return super.headSet(toElement);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		lock.readLock().lock();
		try {
			return super.headSet(toElement, inclusive);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E higher(E e) {
		lock.readLock().lock();
		try {
			return super.higher(e);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E last() {
		lock.readLock().lock();
		try {
			return super.last();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E lower(E e) {
		lock.readLock().lock();
		try {
			return super.lower(e);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E pollFirst() {
		lock.writeLock().lock();
		try {
			return super.pollFirst();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public E pollLast() {
		lock.writeLock().lock();
		try {
			return super.pollLast();
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
	
	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		lock.readLock().lock();
		try {
			return super.subSet(fromElement, fromInclusive, toElement, toInclusive);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		lock.readLock().lock();
		try {
			return super.subSet(fromElement, toElement);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public SortedSet<E> tailSet(E fromElement) {
		lock.readLock().lock();
		try {
			return super.tailSet(fromElement);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		lock.readLock().lock();
		try {
			return super.tailSet(fromElement, inclusive);
		} finally {
			lock.readLock().unlock();
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
	public Object clone() {
		lock.readLock().lock();
		try {
			return new ConcurrentHashSet<>(this);
		} finally {
			lock.readLock().unlock();
		}
	}
}
