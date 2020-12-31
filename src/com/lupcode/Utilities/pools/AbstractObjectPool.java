package com.lupcode.Utilities.pools;

import java.util.concurrent.atomic.AtomicInteger;

/** 
 * Abstract class for pools that implements the simple set/get methods and a constructor
 * @author LupCode.com (Luca Vogels)
 * @since 2020-02-13
 *
 * @param <O> Objects that can be created and recycled
 */
public abstract class AbstractObjectPool<O> implements ObjectPool<O> {

	public static ObjectPoolFullMode DEFAULT_FULL_MODE = ObjectPoolFullMode.BLOCK;
	
	protected int min_keep_count;
	protected long max_total_size, keep_unused_alive;
	protected ObjectPoolFullMode full_mode;
	protected AtomicInteger waiting = new AtomicInteger();

	/**
	 * @param max_total_size the maximum amount of bytes this reuser is allowed to allocate. Negative means unlimited
	 * @param min_keep_count how many objects should be kept in memory at minimum
	 * @param keep_unused_alive milliseconds how long unused objects should be kept in memory. Negative for unlimited
	 * @param full_mode how the reuser should behave if it reaches the maximum total size
	 */
	public AbstractObjectPool(long max_total_size, int min_keep_count, long keep_unused_alive, ObjectPoolFullMode full_mode) {
		this.max_total_size = max_total_size;
		this.min_keep_count = min_keep_count;
		this.keep_unused_alive = keep_unused_alive;
		setFullMode(full_mode);
	}
	
	@Override
	public int getMinKeepCount(){
		return min_keep_count;
	}
	
	@Override
	public void setMinKeepCount(int min_keep_count){
		this.min_keep_count = min_keep_count;
	}
	
	@Override
	public long getKeepUnusedAlive(){
		return keep_unused_alive;
	}
	
	@Override
	public void setKeepUnusedAlive(long keep_unused_alive){
		this.keep_unused_alive = keep_unused_alive;
	}
	
	@Override
	public long getMaxTotalSize(){
		return max_total_size;
	}
	
	@Override
	public void setMaxTotalSize(long max_total_size){
		boolean need_reset = max_total_size >= 0 && getCurrentTotalSize() > max_total_size;
		this.max_total_size = max_total_size;
		if(need_reset)
			clear();
	}
	
	@Override
	public ObjectPoolFullMode getFullMode() {
		return full_mode;
	}

	@Override
	public void setFullMode(ObjectPoolFullMode full_mode) {
		this.full_mode = full_mode!=null ? full_mode : DEFAULT_FULL_MODE;
	}

	@Override
	public String toString() {
		final long current_total_size = getCurrentTotalSize();
		return new StringBuilder(getClass().getSimpleName()).append("{free_count=").append(getFreeCount()).
				append("/").append(getTotalCount()).append("; waiting=").append(waiting.get()).
				append("; memory=").append(current_total_size).
				append("/").append(max_total_size).append(" (").
				append((int)Math.round(current_total_size*100/max_total_size)).
				append("%); keep_unused_alive=").append(keep_unused_alive).append("; full_mode=").
				append(full_mode).append("}").toString();
	}
}
