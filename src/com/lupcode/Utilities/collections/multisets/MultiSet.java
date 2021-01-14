package com.lupcode.Utilities.collections.multisets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Base class for all sets that can hold the 
 * same element multiple times. 
 * All {@link MultiSet}s are thread-safe
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-14
 * @param <E> Element that set should hold
 */
public class MultiSet<E> implements Set<E> {

	protected Map<E, Queue<E>> elements;
	protected int size = 0;
	protected ReadWriteLock lock = new ReentrantReadWriteLock();
	
	/**
	 * Creates a {@link MultiSet}
	 * @param storage Map that should be used to store elements
	 */
	public MultiSet(Map<E, Queue<E>> storage) {
		if(storage == null) throw new NullPointerException("Storage cannot be null");
		this.elements = storage;
	}
	
	protected Queue<E> createQueue(E e){
		Queue<E> q = elements.get(e);
		if(q == null) {
			q = new LinkedList<>();
			elements.put(e, q);
		} return q;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size <= 0;
	}

	@Override
	public boolean contains(Object o) {
		lock.readLock().lock();
		try {
			if(o == null) return elements.containsKey(o);
			Queue<E> q = elements.get(o);
			return q != null && q.contains(o);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			Iterator<Entry<E, Queue<E>>> iterOuter = elements.entrySet().iterator();
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
	public Object[] toArray() {
		lock.readLock().lock();
		try {
			Object[] arr = new Object[size];
			int index = 0;
			for(Queue<E> q : elements.values())
				for(E e : q)
					arr[index++] = e;
			return arr;
		} finally {
			lock.readLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		lock.readLock().lock();
		try {
			T[] arr = Arrays.copyOf(a, size);
			int index = 0;
			for(Queue<E> q : elements.values())
				for(E e : q)
					arr[index++] = (T) e;
			return arr;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean add(E e) {
		lock.writeLock().lock();
		try {
			boolean changed = createQueue(e).add(e);
			if(changed) size++;
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean remove(Object o) {
		lock.writeLock().lock();
		try {
			Queue<E> q = elements.get(o);
			if(q == null) return false;
			boolean changed = q.remove(o);
			if(changed) {
				size--;
				if(q.isEmpty())
					elements.remove(o);
			}
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if(c == null || c.isEmpty()) return true;
		lock.readLock().lock();
		try {
			for(Object o : c) {
				if(o == null) {
					if(!elements.containsKey(o)) return false;
					continue;
				}
				Queue<E> q = elements.get(o);
				if(q == null || !q.contains(o)) return false;
			}
			return true;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(c == null || c.isEmpty()) return false;
		lock.writeLock().lock();
		try {
			boolean changed = false;
			for(E e : c)
				if(createQueue(e).add(e)) {
					size++;
					changed = true;
				}
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(c == null || c.isEmpty()) {
			int prevSize = size;
			clear();
			return prevSize != 0;
		}
		lock.writeLock().lock();
		try {
			boolean changed = false;
			Iterator<Entry<E, Queue<E>>> it = elements.entrySet().iterator();
			while(it.hasNext()) {
				Entry<E, Queue<E>> entry = it.next();
				int prevSize = entry.getValue().size();
				entry.getValue().retainAll(c);
				int afterSize = entry.getValue().size();
				if(prevSize != afterSize) {
					size -= prevSize-afterSize;
					changed = true;
				}
				if(afterSize <= 0)
					it.remove();
			}
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(c == null || c.isEmpty()) return false;
		lock.writeLock().lock();
		try {
			boolean changed = false;
			for(Object o : c) {
				Queue<E> q = elements.get(o);
				if(q == null) continue;
				if(q.remove(o)) {
					size--;
					changed = true;
					if(q.isEmpty()) elements.remove(o);
				}
			}
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		try {
			elements.clear();
			size = 0;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("{size=");
		sb.append(size).append("; elements=[");
		boolean notFirst = false;
		for(Queue<E> q : elements.values())
			for(E e : q) {
				if(notFirst) sb.append(", "); else notFirst = true;
				sb.append(e);
			}
		return sb.append("]}").toString();
	}
}
