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
		boolean v = super.containsAll(c);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public boolean equals(Object o) {
		lock.readLock().lock();
		boolean v = super.equals(o);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		lock.writeLock().lock();
		boolean v = super.removeAll(c);
		lock.writeLock().unlock();
		return v;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		lock.writeLock().lock();
		boolean v = super.retainAll(c);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public boolean add(E e) {
		lock.writeLock().lock();
		boolean v = super.add(e);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		lock.writeLock().lock();
		boolean v = super.addAll(c);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public E ceiling(E e) {
		lock.readLock().lock();
		e = super.ceiling(e);
		lock.readLock().unlock();
		return e;
	}
	
	@Override
	public void clear() {
		lock.writeLock().lock();
		super.clear();
		lock.writeLock().unlock();
	}
	
	@Override
	public boolean contains(Object o) {
		lock.readLock().lock();
		boolean v = super.contains(o);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public Iterator<E> descendingIterator() {
		lock.readLock().lock();
		Iterator<E> it = super.descendingIterator();
		lock.readLock().unlock();
		return it;
	}
	
	@Override
	public NavigableSet<E> descendingSet() {
		lock.readLock().lock();
		NavigableSet<E> set = super.descendingSet();
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public E first() {
		lock.readLock().lock();
		E e = super.first();
		lock.readLock().unlock();
		return e;
	}
	
	@Override
	public E floor(E e) {
		lock.readLock().lock();
		e = super.floor(e);
		lock.readLock().unlock();
		return e;
	}
	
	@Override
	public void forEach(Consumer<? super E> action) {
		lock.writeLock().lock();
		super.forEach(action);
		lock.writeLock().unlock();
	}
	
	@Override
	public SortedSet<E> headSet(E toElement) {
		lock.readLock().lock();
		SortedSet<E> set = super.headSet(toElement);
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		lock.readLock().lock();
		NavigableSet<E> set = super.headSet(toElement, inclusive);
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public E higher(E e) {
		lock.readLock().lock();
		e = super.higher(e);
		lock.readLock().unlock();
		return e;
	}
	
	@Override
	public E last() {
		lock.readLock().lock();
		E e = super.last();
		lock.readLock().unlock();
		return e;
	}
	
	@Override
	public E lower(E e) {
		lock.readLock().lock();
		e = super.lower(e);
		lock.readLock().unlock();
		return e;
	}
	
	@Override
	public E pollFirst() {
		lock.writeLock().lock();
		E e = super.pollFirst();
		lock.writeLock().unlock();
		return e;
	}
	
	@Override
	public E pollLast() {
		lock.writeLock().lock();
		E e = super.pollLast();
		lock.writeLock().unlock();
		return e;
	}
	
	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		boolean v = super.isEmpty();
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public boolean remove(Object o) {
		lock.writeLock().lock();
		boolean v = super.remove(o);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		lock.writeLock().lock();
		boolean v = super.removeIf(filter);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public int size() {
		lock.readLock().lock();
		int v = super.size();
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		lock.readLock().lock();
		NavigableSet<E> set = super.subSet(fromElement, fromInclusive, toElement, toInclusive);
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		lock.readLock().lock();
		SortedSet<E> set = super.subSet(fromElement, toElement);
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public SortedSet<E> tailSet(E fromElement) {
		lock.readLock().lock();
		SortedSet<E> set = super.tailSet(fromElement);
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		lock.readLock().lock();
		NavigableSet<E> set = super.tailSet(fromElement, inclusive);
		lock.readLock().unlock();
		return set;
	}
}
