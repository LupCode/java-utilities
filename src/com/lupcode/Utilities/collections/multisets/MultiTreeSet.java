package com.lupcode.Utilities.collections.multisets;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * {@link MultiSet} that uses a tree structure for storage.
 * This {@link MultiTreeSet} is thread-safe
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-14
 * @param <E> Element that should be hold
 */
public class MultiTreeSet<E> extends MultiSet<E> implements Cloneable, NavigableSet<E>, SortedSet<E> {

	/**
	 * Creates a {@link MultiTreeSet}
	 */
	public MultiTreeSet() {
		super(new TreeMap<>());
	}
	/**
	 * Creates a {@link MultiTreeSet}
	 * @param comparator Comparator that should be used to compare the elements in the set
	 */
	public MultiTreeSet(Comparator<? super E> comparator) {
		super(new TreeMap<>(comparator));
	}

	@Override
	protected MultiTreeSet<E> clone() {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> clone = new MultiTreeSet<>(comparator());
			clone.elements.putAll(elements);
			return clone;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Comparator<? super E> comparator() {
		return ((SortedMap<E, Queue<E>>) elements).comparator();
	}

	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> subSet = new MultiTreeSet<>(comparator());
			subSet.elements = ((SortedMap<E, Queue<E>>) elements).subMap(fromElement, toElement);
			subSet.lock = lock;
			subSet.size = 0;
			for(Queue<E> q : subSet.elements.values())
				subSet.size += q.size();
			return subSet;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public SortedSet<E> headSet(E toElement) {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> subSet = new MultiTreeSet<>(comparator());
			subSet.elements = ((SortedMap<E, Queue<E>>) elements).headMap(toElement);
			subSet.lock = lock;
			subSet.size = 0;
			for(Queue<E> q : subSet.elements.values())
				subSet.size += q.size();
			return subSet;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public SortedSet<E> tailSet(E fromElement) {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> subSet = new MultiTreeSet<>(comparator());
			subSet.elements = ((SortedMap<E, Queue<E>>) elements).tailMap(fromElement);
			subSet.lock = lock;
			subSet.size = 0;
			for(Queue<E> q : subSet.elements.values())
				subSet.size += q.size();
			return subSet;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public E first() {
		lock.readLock().lock();
		try {
			return ((SortedMap<E, Queue<E>>) elements).firstKey();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public E last() {
		lock.readLock().lock();
		try {
			return ((SortedMap<E, Queue<E>>) elements).lastKey();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E lower(E e) {
		lock.readLock().lock();
		try {
			return ((NavigableMap<E, Queue<E>>) elements).lowerKey(e);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E floor(E e) {
		lock.readLock().lock();
		try {
			return ((NavigableMap<E, Queue<E>>) elements).floorKey(e);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E ceiling(E e) {
		lock.readLock().lock();
		try {
			return ((NavigableMap<E, Queue<E>>) elements).ceilingKey(e);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E higher(E e) {
		lock.readLock().lock();
		try {
			return ((NavigableMap<E, Queue<E>>) elements).higherKey(e);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public E pollFirst() {
		lock.writeLock().unlock();
		try {
			do {
				Entry<E, Queue<E>> entry = ((NavigableMap<E, Queue<E>>) elements).firstEntry();
				if(entry == null) return null;
				try {
					E e = entry.getValue().remove();
					if(entry.getValue().isEmpty())
						elements.remove(entry.getKey());
					return e;
				} catch (NoSuchElementException ex) {}
			} while(true);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public E pollLast() {
		lock.writeLock().unlock();
		try {
			do {
				Entry<E, Queue<E>> entry = ((NavigableMap<E, Queue<E>>) elements).lastEntry();
				if(entry == null) return null;
				try {
					E e = entry.getValue().remove();
					if(entry.getValue().isEmpty())
						elements.remove(entry.getKey());
					return e;
				} catch (NoSuchElementException ex) {}
			} while(true);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public NavigableSet<E> descendingSet() {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> subSet = new MultiTreeSet<>(comparator());
			subSet.elements = ((NavigableMap<E, Queue<E>>) elements).descendingMap();
			subSet.lock = lock;
			subSet.size = 0;
			for(Queue<E> q : subSet.elements.values())
				subSet.size += q.size();
			return subSet;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Iterator<E> descendingIterator() {
		return new Iterator<E>() {

			Iterator<Entry<E, Queue<E>>> iterOuter = ((NavigableMap<E, Queue<E>>) elements).descendingMap().entrySet().iterator();
			Entry<E, Queue<E>> currentEntry = null;
			Iterator<E> iterInner = null;
			
			@Override
			public boolean hasNext() {
				lock.readLock().lock();
				try {
					return (iterInner!=null && iterInner.hasNext()) || iterOuter.hasNext();
				} finally {
					lock.readLock().unlock();
				}
			}

			@Override
			public E next() {
				lock.readLock().lock();
				try {
					while(iterInner == null || !iterInner.hasNext()) {
						if(!iterOuter.hasNext()) {
							currentEntry = null;
							iterInner = null;
							return null;
						}
						currentEntry = iterOuter.next();
						if(currentEntry != null)
							iterInner = currentEntry.getValue().iterator();
					}
					return iterInner.next();
				} finally {
					lock.readLock().unlock();
				}
			}
			
			@Override
			public void remove() {
				if(iterInner == null) return;
				lock.writeLock().lock();
				try {
					iterInner.remove();
					size--;
					if(currentEntry != null && currentEntry.getValue().isEmpty())
						elements.remove(currentEntry.getKey());
				} finally {
					lock.writeLock().unlock();
				}
			}
		};
	}
	
	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> subSet = new MultiTreeSet<>(comparator());
			subSet.elements = ((NavigableMap<E, Queue<E>>) elements).subMap(fromElement, fromInclusive, toElement, toInclusive);
			subSet.lock = lock;
			subSet.size = 0;
			for(Queue<E> q : subSet.elements.values())
				subSet.size += q.size();
			return subSet;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> subSet = new MultiTreeSet<>(comparator());
			subSet.elements = ((NavigableMap<E, Queue<E>>) elements).headMap(toElement, inclusive);
			subSet.lock = lock;
			subSet.size = 0;
			for(Queue<E> q : subSet.elements.values())
				subSet.size += q.size();
			return subSet;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		lock.readLock().lock();
		try {
			MultiTreeSet<E> subSet = new MultiTreeSet<>(comparator());
			subSet.elements = ((NavigableMap<E, Queue<E>>) elements).tailMap(fromElement, inclusive);
			subSet.lock = lock;
			subSet.size = 0;
			for(Queue<E> q : subSet.elements.values())
				subSet.size += q.size();
			return subSet;
		} finally {
			lock.readLock().unlock();
		}
	}
}
