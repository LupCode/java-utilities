package com.lupcode.Utilities.collections.scheduled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * {@link BlockingQueue} that allows to efficiently schedule elements 
 * so they become available after a certain amount of time. 
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-07
 * @param <E> Element that should be held by queue
 */
public class ScheduledLinkedBlockingQueue<E> implements ScheduledBlockingQueue<E> {
	
	protected long totalSize = 0, capacity = -1;
	protected boolean allowNull = true;
	protected TreeMap<Long, Queue<E>> elements = new TreeMap<>();
	protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected Condition condNewItem = lock.writeLock().newCondition();
	protected Condition condNewSpace = lock.writeLock().newCondition();
	
	/**
	 * Creates a new queue with unlimited capacity
	 */
	public ScheduledLinkedBlockingQueue() {
		
	}
	
	/**
	 * Creates a new queue with unlimited capacity 
	 * and a given collection of elements already added to it
	 * @param c Collection of elements that should directly be added
	 */
	public ScheduledLinkedBlockingQueue(Collection<E> c) {
		addAll(c);
	}
	
	/**
	 * Creates a new queue with a fixed capacity
	 * @param capacity Capacity how many elements can be hold at maximum
	 */
	public ScheduledLinkedBlockingQueue(long capacity) {
		this.capacity = capacity;
	}
	
	/**
	 * Sets how many elements this queue can hold at maximum. 
	 * If zero or negative no limit is set
	 * @param capacity Maximum amount of elements (zero or negative for no limit)
	 */
	public void setCapacity(long capacity) {
		lock.writeLock().lock();
		this.capacity = capacity;
		condNewSpace.signalAll();
		lock.writeLock().unlock();
	}
	
	/**
	 * Returns how many elements this queue can hold at maximum. 
	 * If zero or negative no limit set
	 * @return Maximum amount of elements (zero or negative for no limit)
	 */
	public long getCapacity() {
		return capacity;
	}
	
	/**
	 * Returns if null as element value is allowed. 
	 * If not methods will throw a {@link NullPointerException}
	 * @return True if null as element is allowed
	 */
	public boolean isNullAllowed() {
		return allowNull;
	}
	
	/**
	 * Sets if null as element value is allowed. 
	 * If {@code false} then methods will throw a {@link NullPointerException}
	 * @param nullable If null value is valid
	 */
	public void setNullAllowed(boolean nullable) {
		this.allowNull = nullable;
	}
	
	protected Queue<E> getQueueForInsert(long time){
		Queue<E> q = elements.get(time);
		if(q == null) {
			q = new LinkedList<>();
			elements.put(time, q);
		} return q;
	}

	@Override
	public E remove() throws NoSuchElementException {
		lock.writeLock().lock();
		Entry<Long, Queue<E>> first = elements.firstEntry();
		if(first == null || first.getKey() > System.currentTimeMillis()) {
			lock.writeLock().unlock();
			throw new NoSuchElementException();
		}
		try {
			E e = first.getValue().remove();
			if(first.getValue().isEmpty()) elements.remove(first.getKey());
			totalSize--;
			condNewSpace.signalAll();
			lock.writeLock().unlock();
			return e;
		} catch (Exception ex) {
			elements.remove(first.getKey());
			lock.writeLock().unlock();
		}
		return remove();
	}

	@Override
	public E poll() {
		lock.writeLock().lock();
		Entry<Long, Queue<E>> first = elements.firstEntry();
		if(first == null || first.getKey() > System.currentTimeMillis()) {
			lock.writeLock().unlock();
			return null;
		}
		try {
			E e = first.getValue().remove();
			if(first.getValue().isEmpty()) elements.remove(first.getKey());
			totalSize--;
			condNewSpace.signalAll();
			lock.writeLock().unlock();
			return e;
		} catch (Exception ex) {
			elements.remove(first.getKey());
			lock.writeLock().unlock();
		}
		return poll();
	}

	@Override
	public E element() throws NoSuchElementException {
		lock.readLock().lock();
		Entry<Long, Queue<E>> first = elements.firstEntry();
		if(first == null || first.getKey() > System.currentTimeMillis()) {
			lock.readLock().unlock();
			throw new NoSuchElementException();
		}
		try {
			E e = first.getValue().element();
			lock.readLock().unlock();
			return e;
		} catch (Exception ex) {
			elements.remove(first.getKey());
			lock.readLock().unlock();
		}
		return element();
	}

	@Override
	public E peek() {
		lock.readLock().lock();
		Entry<Long, Queue<E>> first = elements.firstEntry();
		if(first == null || first.getKey() > System.currentTimeMillis()) {
			lock.readLock().unlock();
			return null;
		}
		E e = first.getValue().peek();
		lock.readLock().unlock();
		return e;
	}

	@Override
	/** Only amount of elements currently available (not included scheduled elements) */
	public int size() {
		lock.readLock().lock();
		int count = 0;
		Iterator<Entry<Long, Queue<E>>> it = elements.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long, Queue<E>> entry = it.next();
			if(entry == null || entry.getValue() == null || entry.getValue().isEmpty()) {
				it.remove();
				continue;
			}
			if(entry.getKey() <= System.currentTimeMillis()) {
				count += entry.getValue().size();
			} else break;
		}
		lock.readLock().unlock();
		return count;
	}
	
	@Override
	public long getTotalSize() {
		return totalSize;
	}

	@Override
	/** Only true if currently elements are available (not included scheduled elements) */
	public boolean isEmpty() {
		Entry<Long, Queue<E>> first = elements.firstEntry();
		return first == null || first.getKey() > System.currentTimeMillis();
	}
	
	@Override
	public boolean isCompletelyEmpty() {
		return elements.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			protected Iterator<Entry<Long, Queue<E>>> outer = elements.entrySet().iterator();
			protected Long currKey = null;
			protected Iterator<E> inner = null;
			
			@Override
			public synchronized boolean hasNext() {
				lock.readLock().lock();
				if(inner != null && inner.hasNext()) {
					lock.readLock().unlock();
					return true;
				}
				if(!outer.hasNext()) {
					currKey = null;
					lock.readLock().unlock();
					return false;
				}
				Entry<Long, Queue<E>> entry = outer.next();
				if(entry == null || entry.getValue() == null) {
					lock.readLock().unlock();
					return hasNext();
				}
				inner = entry.getValue().iterator();
				if(!inner.hasNext()) {
					lock.readLock().unlock();
					return hasNext();
				}
				currKey = entry.getKey();
				lock.readLock().unlock();
				return true;
			}

			@Override
			public synchronized E next() {
				if(inner == null) return null;
				lock.readLock().lock();
				E e = inner.next();
				lock.readLock().unlock();
				return e;
			}
			
			@Override
			public void remove() {
				if(inner == null) return;
				lock.writeLock().lock();
				totalSize--;
				inner.remove();
				if(currKey != null) {
					Queue<E> q = elements.get(currKey);
					if(q == null || q.isEmpty()) {
						elements.remove(currKey);
						currKey = null;
					}
				}
				condNewSpace.signalAll();
				lock.writeLock().unlock();
			}
		};
	}

	@Override
	public Object[] toArray() {
		lock.readLock().lock();
		ArrayList<E> list = new ArrayList<>();
		for(Entry<Long, Queue<E>> entry : elements.entrySet())
			if(entry.getKey() <= System.currentTimeMillis())
				list.addAll(entry.getValue());
			else break;
		lock.readLock().unlock();
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		lock.readLock().lock();
		ArrayList<E> list = new ArrayList<>();
		for(Entry<Long, Queue<E>> entry : elements.entrySet())
			if(entry.getKey() <= System.currentTimeMillis())
				list.addAll(entry.getValue());
			else break;
		lock.readLock().unlock();
		return list.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if(c == null || c.isEmpty()) return true;
		lock.readLock().lock();
		try {
			for(Object o : c) {
				boolean notFound = true;
				for(Queue<E> q : elements.values())
					if(q != null && q.contains(o)) { notFound=false; break; }
				if(notFound) {
					lock.readLock().unlock();
					return false;
				}
			}
			lock.readLock().unlock();
		} catch (Exception ex) {
			lock.readLock().unlock();
			throw ex;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAllAt(c, System.currentTimeMillis());
	}
	
	@Override
	public boolean addAllIn(Collection<? extends E> c, long duration) {
		return addAllAt(c, System.currentTimeMillis() + duration);
	}
	
	@Override
	public boolean addAllAt(Collection<? extends E> c, long time) {
		if(c == null || c.isEmpty()) return false;
		lock.writeLock().lock();
		boolean changed = false;
		try {
			Queue<E> q = getQueueForInsert(time);
			for(E o : c) {
				if(o == null || !allowNull) throw new NullPointerException("Element cannot be null");
				if(capacity > 0 && totalSize > capacity)
					throw new IllegalStateException("Maximum capacity of "+capacity+" reached");
				changed &= q.add(o);
				totalSize++;
			}
			if(q.isEmpty()) elements.remove(time);
			if(changed) condNewItem.signalAll();
			lock.writeLock().unlock();
		} catch (Exception ex) {
			lock.writeLock().unlock();
			throw ex;
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(c == null || c.isEmpty()) return false;
		lock.writeLock().lock();
		boolean changed = false;
		try {
			long newTotalSize = 0;
			Iterator<Entry<Long, Queue<E>>> it = elements.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Long, Queue<E>> entry = it.next();
				if(entry == null || entry.getValue() == null || entry.getValue().isEmpty()) { 
					it.remove();
					continue;
				}
				changed |= entry.getValue().removeAll(c);
				newTotalSize += entry.getValue().size();
				if(entry.getValue().isEmpty()) it.remove();
			}
			this.totalSize = newTotalSize;
			if(changed) condNewSpace.signalAll();
			lock.writeLock().unlock();
		} catch (Exception ex) {
			lock.writeLock().unlock();
			throw ex;
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(c == null || c.isEmpty()) {
			boolean isFullyEmpty = isCompletelyEmpty();
			clear();
			return !isFullyEmpty;
		}
		lock.writeLock().lock();
		long newTotalSize = 0;
		boolean changed = false;
		Iterator<Entry<Long, Queue<E>>> it = elements.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long, Queue<E>> entry = it.next();
			if(entry == null || entry.getValue() == null || entry.getValue().isEmpty()) {
				it.remove();
				continue;
			}
			changed |= entry.getValue().retainAll(c);
			newTotalSize += entry.getValue().size();
			if(entry.getValue().isEmpty())
				it.remove();
		}
		this.totalSize = newTotalSize;
		if(changed) condNewSpace.signalAll();
		lock.writeLock().unlock();
		return changed;
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		elements.clear();
		totalSize = 0;
		condNewSpace.signalAll();
		lock.writeLock().unlock();
	}

	@Override
	public boolean add(E e) {
		return addAt(e, System.currentTimeMillis());
	}
	
	@Override
	public boolean addIn(E e, long duration) {
		return addAt(e, System.currentTimeMillis() + duration);
	}

	@Override
	public boolean addAt(E e, long time) {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		lock.writeLock().lock();
		if(capacity > 0 && totalSize > capacity) {
			lock.writeLock().unlock();
			throw new IllegalStateException("Maximum capacity of "+capacity+" reached");
		}
		Queue<E> q = getQueueForInsert(time);
		boolean changed = q.add(e);
		totalSize++;
		if(changed) condNewItem.signalAll();
		lock.writeLock().unlock();
		System.out.println("ADDED ELEMENT FOR "+time+" ..."); // TODO REMOVE
		return changed;
	}

	@Override
	public boolean offer(E e) {
		return offerAt(e, System.currentTimeMillis());
	}
	
	@Override
	public boolean offerIn(E e, long duration) {
		return offerAt(e, System.currentTimeMillis() + duration);
	}

	@Override
	public boolean offerAt(E e, long time) {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		lock.writeLock().lock();
		if(capacity > 0 && totalSize > capacity) {
			lock.writeLock().unlock();
			return false;
		}
		Queue<E> q = getQueueForInsert(time);
		boolean changed = q.add(e);
		totalSize++;
		if(changed) condNewItem.signalAll();
		lock.writeLock().unlock();
		return changed;
	}
	
	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		timeout = unit.toMillis(timeout);
		lock.writeLock().lock();
		long start = System.currentTimeMillis(), wait;
		boolean full = false;
		while((full = capacity > 0 && totalSize > capacity) && (wait = timeout-(System.currentTimeMillis()-start)) > 0)
			condNewSpace.await(wait, unit);
		if(full) {
			lock.writeLock().unlock();
			return false;
		}
		Queue<E> q = getQueueForInsert(System.currentTimeMillis());
		q.add(e);
		totalSize++;
		condNewItem.signalAll();
		lock.writeLock().unlock();
		return true;
	}

	@Override
	public void put(E e) throws InterruptedException {
		putAt(e, System.currentTimeMillis());
	}
	
	@Override
	public void putIn(E e, long duration) throws InterruptedException {
		putAt(e, System.currentTimeMillis() + duration);
	}

	@Override
	public void putAt(E e, long time) throws InterruptedException {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		lock.writeLock().lock();
		while(capacity > 0 && totalSize > capacity)
			condNewSpace.await();
		Queue<E> q = getQueueForInsert(time);
		boolean changed = q.add(e);
		totalSize++;
		if(changed) condNewItem.signalAll();
		lock.writeLock().unlock();
	}

	@Override
	public E take() throws InterruptedException {
		lock.writeLock().lock();
		Entry<Long, Queue<E>> first = null;
		do {
			first = elements.firstEntry();
			if(first == null) {
				condNewItem.await();
				continue;
			}
			long now = System.currentTimeMillis();
			if(first.getKey() > now){
				condNewItem.await(Math.max(0, first.getKey()-now), TimeUnit.MILLISECONDS);
				first = null;
			}
		} while(first != null);
		try {
			@SuppressWarnings("null")
			E e = first.getValue().remove();
			if(first.getValue().isEmpty()) elements.remove(first.getKey());
			totalSize--;
			condNewSpace.signalAll();
			lock.writeLock().unlock();
			return e;
		} catch (Exception ex) {
			elements.remove(first.getKey());
			lock.writeLock().unlock();
		}
		return take();
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		timeout = unit.toMillis(timeout);
		lock.writeLock().lock();
		long start = System.currentTimeMillis(), wait;
		Entry<Long, Queue<E>> first = null;
		while((wait = timeout-(System.currentTimeMillis()-start)) > 0) {
			first = elements.firstEntry();
			if(first == null) {
				condNewItem.await(wait, unit);
				continue;
			}
			long now = System.currentTimeMillis();
			if(first.getKey() > now){
				condNewItem.await(Math.min(wait, first.getKey()-now), TimeUnit.MILLISECONDS);
				first = null;
			}
		}
		if(first == null) {
			lock.writeLock().unlock();
			return null;
		}
		try {
			E e = first.getValue().remove();
			if(first.getValue().isEmpty()) elements.remove(first.getKey());
			totalSize--;
			condNewSpace.signalAll();
			lock.writeLock().unlock();
			return e;
		} catch (Exception ex) {
			elements.remove(first.getKey());
			lock.writeLock().unlock();
		}
		return null;
	}

	@Override
	public int remainingCapacity() {
		return (int) (capacity > 0 ? capacity - totalSize : -1);
	}

	@Override
	public boolean remove(Object o) {
		lock.writeLock().lock();
		Iterator<Entry<Long, Queue<E>>> it = elements.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long, Queue<E>> entry = it.next();
			if(entry == null || entry.getValue() == null || entry.getValue().isEmpty()) {
				it.remove();
				continue;
			}
			if(entry.getValue().remove(o)) {
				if(entry.getValue().isEmpty()) it.remove();
				totalSize--;
				condNewSpace.signalAll();
				lock.writeLock().unlock();
				return true;
			}
		}
		lock.writeLock().unlock();
		return false;
	}

	@Override
	public boolean contains(Object o) {
		lock.readLock().lock();
		for(Queue<E> q : elements.values())
			if(q.contains(o)) {
				lock.readLock().unlock();
				return true;
			}
		lock.readLock().unlock();
		return false;
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		if(c == null) throw new NullPointerException("Collection cannot be null");
		lock.writeLock().lock();
		int count = 0;
		Iterator<Entry<Long, Queue<E>>> it = elements.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long, Queue<E>> entry = it.next();
			if(entry == null || entry.getValue() == null || entry.getValue().isEmpty()) {
				it.remove();
				continue;
			}
			if(entry.getKey() <= System.currentTimeMillis()) {
				c.addAll(entry.getValue());
				totalSize -= entry.getValue().size();
				it.remove();
			} else break;
		}
		if(count > 0) condNewSpace.signalAll();
		lock.writeLock().unlock();
		return count;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		if(c == null) throw new NullPointerException("Collection cannot be null");
		lock.writeLock().lock();
		int count = 0;
		Iterator<Entry<Long, Queue<E>>> it = elements.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long, Queue<E>> entry = it.next();
			if(entry == null || entry.getValue() == null || entry.getValue().isEmpty()) {
				it.remove();
				continue;
			}
			if(entry.getKey() <= System.currentTimeMillis()) {
				while(!entry.getValue().isEmpty() && count < maxElements) {
					c.add(entry.getValue().remove());
					count++;
					totalSize--;
				}
				if(entry.getValue().isEmpty()) it.remove();
			} else break;
		}
		if(count > 0) condNewSpace.signalAll();
		lock.writeLock().unlock();
		return count;
	}
}
