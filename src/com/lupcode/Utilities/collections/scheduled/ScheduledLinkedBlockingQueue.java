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
	protected Condition condNotEmpty = lock.writeLock().newCondition();
	protected Condition condNotFull = lock.writeLock().newCondition();
	
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
		try {
			this.capacity = capacity;
			if(totalSize < capacity) condNotFull.signalAll();
		} finally {
			lock.writeLock().unlock();
		}
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
		try {
			Entry<Long, Queue<E>> first = elements.firstEntry();
			if(first == null || first.getKey() > System.currentTimeMillis())
				throw new NoSuchElementException();
			try {
				E e = first.getValue().remove();
				if(first.getValue().isEmpty()) elements.remove(first.getKey());
				totalSize--;
				if(totalSize < capacity) condNotFull.signalAll();
				return e;
			} catch (Exception ex) {
				elements.remove(first.getKey());
			}
			return remove();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public E poll() {
		lock.writeLock().lock();
		try {
			Entry<Long, Queue<E>> first = elements.firstEntry();
			if(first == null || first.getKey() > System.currentTimeMillis())
				return null;
			try {
				E e = first.getValue().remove();
				if(first.getValue().isEmpty()) elements.remove(first.getKey());
				totalSize--;
				if(totalSize < capacity) condNotFull.signalAll();
				return e;
			} catch (Exception ex) {
				elements.remove(first.getKey());
			}
			return poll();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public E element() throws NoSuchElementException {
		lock.readLock().lock();
		try {
			Entry<Long, Queue<E>> first = elements.firstEntry();
			if(first == null || first.getKey() > System.currentTimeMillis())
				throw new NoSuchElementException();
			try {
				return first.getValue().element();
			} catch (Exception ex) {
				elements.remove(first.getKey());
			}
			return element();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public E peek() {
		lock.readLock().lock();
		try {
			Entry<Long, Queue<E>> first = elements.firstEntry();
			if(first == null || first.getKey() > System.currentTimeMillis())
				return null;
			return first.getValue().peek();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	/** Only amount of elements currently available (not included scheduled elements) */
	public int size() {
		lock.readLock().lock();
		try {
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
			return count;
		} finally {
			lock.readLock().unlock();
		}
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
				try {
					if(inner != null && inner.hasNext()) return true;
					if(!outer.hasNext()) {
						currKey = null;
						return false;
					}
					Entry<Long, Queue<E>> entry = outer.next();
					if(entry == null || entry.getValue() == null) return hasNext();
					inner = entry.getValue().iterator();
					if(!inner.hasNext()) return hasNext();
					currKey = entry.getKey();
					return true;
				} finally {
					lock.readLock().unlock();
				}
			}

			@Override
			public synchronized E next() {
				if(inner == null) return null;
				lock.readLock().lock();
				try {
					return inner.next();
				} finally {
					lock.readLock().unlock();
				}
			}
			
			@Override
			public void remove() {
				if(inner == null) return;
				lock.writeLock().lock();
				try {
					totalSize--;
					inner.remove();
					if(currKey != null) {
						Queue<E> q = elements.get(currKey);
						if(q == null || q.isEmpty()) {
							elements.remove(currKey);
							currKey = null;
						}
					}
					if(totalSize < capacity) condNotFull.signalAll();
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
			ArrayList<E> list = new ArrayList<>();
			for(Entry<Long, Queue<E>> entry : elements.entrySet())
				if(entry.getKey() <= System.currentTimeMillis())
					list.addAll(entry.getValue());
				else break;
			return list.toArray();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public <T> T[] toArray(T[] a) {
		lock.readLock().lock();
		try {
			ArrayList<E> list = new ArrayList<>();
			for(Entry<Long, Queue<E>> entry : elements.entrySet())
				if(entry.getKey() <= System.currentTimeMillis())
					list.addAll(entry.getValue());
				else break;
			return list.toArray(a);
		} finally {
			lock.readLock().unlock();
		}
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
					return false;
				}
			}
			return true;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAllAt(System.currentTimeMillis(), c);
	}
	
	@Override
	public boolean addAllIn(long duration, Collection<? extends E> c) {
		return addAllAt(System.currentTimeMillis() + duration, c);
	}
	
	@Override
	public boolean addAllAt(long time, Collection<? extends E> c) {
		if(c == null || c.isEmpty()) return false;
		lock.writeLock().lock();
		try {
			boolean changed = false;
			Queue<E> q = getQueueForInsert(time);
			for(E o : c) {
				if(o == null || !allowNull) throw new NullPointerException("Element cannot be null");
				if(capacity > 0 && totalSize > capacity)
					throw new IllegalStateException("Maximum capacity of "+capacity+" reached");
				changed &= q.add(o);
				totalSize++;
			}
			if(q.isEmpty()) elements.remove(time);
			if(changed) condNotEmpty.signalAll();
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
			if(changed && totalSize < capacity) condNotFull.signalAll(); 
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(c == null || c.isEmpty()) {
			boolean isFullyEmpty = isCompletelyEmpty();
			clear();
			return !isFullyEmpty;
		}
		lock.writeLock().lock();
		try {
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
			if(changed && totalSize < capacity) condNotFull.signalAll();
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
			totalSize = 0;
			condNotFull.signalAll();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean add(E e) {
		return addAt(System.currentTimeMillis(), e);
	}
	
	@Override
	public boolean addIn(long duration, E e) {
		return addAt(System.currentTimeMillis() + duration, e);
	}

	@Override
	public boolean addAt(long time, E e) {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		lock.writeLock().lock();
		try {
			if(capacity > 0 && totalSize > capacity)
				throw new IllegalStateException("Maximum capacity of "+capacity+" reached");
			Queue<E> q = getQueueForInsert(time);
			boolean changed = q.add(e);
			totalSize++;
			if(changed) condNotEmpty.signalAll();
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean offer(E e) {
		return offerAt(System.currentTimeMillis(), e);
	}
	
	@Override
	public boolean offerIn(long duration, E e) {
		return offerAt(System.currentTimeMillis() + duration, e);
	}

	@Override
	public boolean offerAt(long time, E e) {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		lock.writeLock().lock();
		try {
			if(capacity > 0 && totalSize > capacity) return false;
			Queue<E> q = getQueueForInsert(time);
			boolean changed = q.add(e);
			totalSize++;
			if(changed) condNotEmpty.signalAll();
			return changed;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return offerAt(System.currentTimeMillis(), e, timeout, unit);
	}
	
	@Override
	public boolean offerIn(long duration, E e, long timeout, TimeUnit unit) throws InterruptedException {
		return offerAt(System.currentTimeMillis() + duration, e, timeout, unit);
	}

	@Override
	public boolean offerAt(long time, E e, long timeout, TimeUnit unit) throws InterruptedException {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		timeout = unit.toMillis(timeout);
		lock.writeLock().lock();
		try {
			long start = System.currentTimeMillis(), wait;
			boolean full = false;
			while((full = capacity > 0 && totalSize >= capacity) && (wait = timeout-(System.currentTimeMillis()-start)) > 0)
				condNotFull.await(wait, TimeUnit.MILLISECONDS);
			if(full) return false;
			Queue<E> q = getQueueForInsert(time);
			q.add(e);
			totalSize++;
			condNotEmpty.signalAll();
			return true;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void put(E e) throws InterruptedException {
		putAt(System.currentTimeMillis(), e);
	}
	
	@Override
	public void putIn(long duration, E e) throws InterruptedException {
		putAt(System.currentTimeMillis() + duration, e);
	}

	@Override
	public void putAt(long time, E e) throws InterruptedException {
		if(e == null && !allowNull) throw new NullPointerException("Element cannot be null");
		lock.writeLock().lock();
		try {
			while(capacity > 0 && totalSize >= capacity)
				condNotFull.await();
			Queue<E> q = getQueueForInsert(time);
			boolean changed = q.add(e);
			totalSize++;
			if(changed) condNotEmpty.signalAll();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public E take() throws InterruptedException {
		lock.writeLock().lock();
		try {
			Entry<Long, Queue<E>> first = null;
			do {
				first = elements.firstEntry();
				if(first == null) {
					condNotEmpty.await();
					continue;
				}
				long now = System.currentTimeMillis();
				if(first.getKey() > now){
					condNotEmpty.await(Math.max(0, first.getKey()-now), TimeUnit.MILLISECONDS);
					first = null;
				}
			} while(first != null);
			try {
				@SuppressWarnings("null")
				E e = first.getValue().remove();
				if(first.getValue().isEmpty()) elements.remove(first.getKey());
				totalSize--;
				if(totalSize < capacity) condNotFull.signalAll();
				return e;
			} catch (Exception ex) {
				elements.remove(first.getKey());
			}
			return take();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		timeout = unit.toMillis(timeout);
		lock.writeLock().lock();
		try {
			long start = System.currentTimeMillis(), wait;
			Entry<Long, Queue<E>> first = null;
			while(first == null && (wait = timeout-(System.currentTimeMillis()-start)) > 0) {
				first = elements.firstEntry();
				if(first == null) {
					condNotEmpty.await(wait, TimeUnit.MILLISECONDS);
					continue;
				}
				long now = System.currentTimeMillis();
				if(first.getKey() > now){
					condNotEmpty.await(Math.min(wait, first.getKey()-now), TimeUnit.MILLISECONDS);
					first = null;
				}
			}
			if(first == null) return null;
			try {
				E e = first.getValue().remove();
				if(first.getValue().isEmpty()) elements.remove(first.getKey());
				totalSize--;
				if(totalSize < capacity) condNotFull.signalAll();
				return e;
			} catch (Exception ex) {
				elements.remove(first.getKey());
			}
			return null;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int remainingCapacity() {
		return (int) (capacity > 0 ? capacity - totalSize : -1);
	}

	@Override
	public boolean remove(Object o) {
		lock.writeLock().lock();
		try {
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
					if(totalSize < capacity) condNotFull.signalAll();
					return true;
				}
			}
			return false;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean contains(Object o) {
		lock.readLock().lock();
		try {
			for(Queue<E> q : elements.values())
				if(q.contains(o)) return true;
			return false;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		if(c == null) throw new NullPointerException("Collection cannot be null");
		lock.writeLock().lock();
		try {
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
			if(totalSize < capacity) condNotFull.signalAll();
			return count;
		} finally {
			lock.writeLock().unlock();
		}
		
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		if(c == null) throw new NullPointerException("Collection cannot be null");
		lock.writeLock().lock();
		try {
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
			if(totalSize < capacity) condNotFull.signalAll();
			return count;
		} finally {
			lock.writeLock().unlock();
		}
	}
}
