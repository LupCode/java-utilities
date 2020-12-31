package com.lupcode.Utilities.pools;

import com.lupcode.Utilities.exceptions.ObjectPoolCapacityException;

/** Interface that every object pool implements. 
 * Object pools can create and recycle objects
 * @author LupCode.com (Luca Vogels)
 * @since 2020-02-11
 *
 * @param <O> Object type pool that can create and recycle
 */
public interface ObjectPool<O> {
	
	/**
	 * Returns how many objects should always be kept in memory
	 * @return Amount of objects kept in memory
	 */
	public abstract int getMinKeepCount();
	
	/** Sets how many objects should always be kept in memory
	 * @param minKeepCount Minimum amount of objects kept in memory
	 */
	public abstract void setMinKeepCount(int minKeepCount);
	
	/**
	 * @return milliseconds how long unused objects will be kept in memory. 
	 * If negative then all allocated objects will be kept without releasing
	 */
	public abstract long getKeepUnusedAlive();
	
	/** Sets how long unused objects should be kept in memory
	 * @param keep_unused_alive milliseconds how long to keep in memory. 
	 * If negative then all allocated objects will be kept without releasing
	 */
	public abstract void setKeepUnusedAlive(long keep_unused_alive);
	
	/**
	 * @return maximum capacity in bytes that this reuser can allocate. 
	 * If negative then this reuser can allocate unlimited memory
	 */
	public abstract long getMaxTotalSize();
	
	/**
	 * Sets the maximum capacity in bytes that this reuser can allocate. 
	 * Also clears this reuser if current total size is greater then new max size
	 * @param max_total_size max size in bytes. 
	 * If negative then this reuser can allocate unlimited memory
	 */
	public abstract void setMaxTotalSize(long max_total_size);
	
	/**
	 * Returns the current memory usage in bytes
	 * @return Amount of bytes in memory
	 */
	public abstract long getCurrentTotalSize();
	
	/**
	 * Returns how many objects are currently unused
	 * @return Amount of objects unused
	 */
	public abstract int getFreeCount();
	
	/**
	 * Returns how many objects are currently allocated
	 * @return Amount of allocated objects
	 */
	public abstract int getTotalCount();
	
	/**
	 * Returns the mode that is set and determines how the pool behaves if its capacity is reached
	 * @return Mode that determines behavior of pool if it is full
	 */
	public abstract ObjectPoolFullMode getFullMode();
	
	/**
	 * Sets the mode that determines the behavior of the pool if it is full
	 * @param mode Mode that should be set
	 */
	public abstract void setFullMode(ObjectPoolFullMode full_mode);
	
	/**
	 * Releases all allocated objects
	 */
	public abstract void clear();
	
	/**
	 * Returns true if capacity of pool is fully reached and no further objects can be allocated
	 * @return True if full and no more objects can be allocated
	 */
	public abstract boolean isFull();
	
	/**
	 * Tries to allocate a new/recycled object and returns it. 
	 * Object must be released afterwards by calling {@link ObjectPool#releaseObject(Object)}. 
	 * Depending on the {@link ObjectPoolFullMode} this function may return null, block or 
	 * throws an {@link ObjectPoolCapacityException}
	 * @return Object that was allocated (or null depending on full mode)
	 * @throws ObjectPoolCapacityException if capacity of pool is reached and 
	 * full mode is set to {@link ObjectPoolFullMode#THROW_EXCEPTION}
	 */
	public abstract O allocateFreeObject() throws ObjectPoolCapacityException;
	
	/** 
	 * Releases the allocated objects for reusing it
	 * @param object Object that should be released for reusing
	 */
	public abstract void releaseObject(O object);
	
	/**
	 * Releases the memory of unused objects that have exceeded expire time
	 */
	public abstract void clearUnused();
}
