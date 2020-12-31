package com.lupcode.Utilities.pools;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lupcode.Utilities.exceptions.ObjectPoolCapacityException;

public class ByteArrayOutputStreamPool extends AbstractObjectPool<ByteArrayOutputStream> {
	
	protected int initial_capacity;
	protected Lock lock = new ReentrantLock();
	protected HashMap<ByteArrayOutputStream, Long> objects = new HashMap<>();
	protected Queue<ByteArrayOutputStream> free_objects = new LinkedList<>();

	public ByteArrayOutputStreamPool(int initial_capacity, long max_total_size, int min_keep_count, long keep_unused_alive, ObjectPoolFullMode full_mode) {
		super(max_total_size, min_keep_count, keep_unused_alive, full_mode);
		this.initial_capacity = initial_capacity;
	}
	
	/**
	 * @return the initial capacity in bytes that each {@link ByteArrayOutputStream} has
	 */
	public int getInitialCapacity() {
		return initial_capacity;
	}
	
	/** Sets the initial capacity in bytes that each {@link ByteArrayOutputStream} has
	 * @param initial_capacity in bytes
	 */
	public void setInitialCapacity(int initial_capacity) {
		this.initial_capacity = initial_capacity;
	}
	
	@Override
	public long getCurrentTotalSize(){
		long sum = 0;
		int size;
		Iterator<ByteArrayOutputStream> it = objects.keySet().iterator();
		while(it.hasNext()) {
			size = it.next().size();
			sum += size > 0 ? size : initial_capacity;
		} return sum;
	}
	
	@Override
	public int getFreeCount(){
		return free_objects.size();
	}
	
	@Override
	public int getTotalCount(){
		return objects.size();
	}

	@Override
	public void clear() {
		lock.lock();
		objects.clear();
		free_objects.clear();
		lock.unlock();
	}

	@Override
	public boolean isFull() {
		return getCurrentTotalSize() >= max_total_size;
	}

	@Override
	public ByteArrayOutputStream allocateFreeObject() throws ObjectPoolCapacityException {
		lock.lock();
		if(isFull() && free_objects.isEmpty()){
			switch (full_mode) {
			case RETURN_NULL: lock.unlock(); return null;
			case THROW_EXCEPTION: lock.unlock(); throw new ObjectPoolCapacityException();
			default: break;
			}
		}
		ByteArrayOutputStream output = null;
		do {
			
			output = free_objects.poll();
			if(output!=null){ output.reset(); break; }
			
			if(!isFull()){
				output = new ByteArrayOutputStream(initial_capacity);
				break;
			}
			
			lock.unlock();
			waiting.incrementAndGet();
			synchronized (this) {
				try { wait(); } catch (InterruptedException e) { e.printStackTrace(); }
			}
			lock.lock();
			waiting.decrementAndGet();
			
		} while(output==null);

		objects.put(output, System.currentTimeMillis());
		lock.unlock();
		return output;
	}

	@Override
	public void releaseObject(ByteArrayOutputStream output) {
		if(output!=null){
			lock.lock();
			Long previous = objects.put(output, System.currentTimeMillis());
			if(previous!=null)
				free_objects.add(output);
			else
				objects.remove(output); // object wasn't created by this reuser
			lock.unlock();
		}
		clearUnused();
	}
	
	@Override
	public void clearUnused(){
		// notify waiting threads
		synchronized (this) {
			notifyAll();
		}
		
		if(this.keep_unused_alive<0){ return;} 
		
		// clean unused buffers
		lock.lock();
		final long current_time = System.currentTimeMillis();
		ByteArrayOutputStream output;
		while(!free_objects.isEmpty() && objects.size() > min_keep_count){
			output = free_objects.peek();
			if(current_time - objects.get(output) >= this.keep_unused_alive){
				free_objects.poll();
				objects.remove(output);
			} else { break; }
		}
		lock.unlock();
	}
}
