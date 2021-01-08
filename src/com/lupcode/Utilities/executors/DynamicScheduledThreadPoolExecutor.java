package com.lupcode.Utilities.executors;

import java.util.concurrent.TimeUnit;

import com.lupcode.Utilities.collections.scheduled.ScheduledBlockingQueue;
import com.lupcode.Utilities.collections.scheduled.ScheduledLinkedBlockingQueue;

/**
 * Extension to the {@link DynamicThreadPoolExecutor} which allows 
 * to schedule tasks so they get executed no directly but after a certain time
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-07
 */
public class DynamicScheduledThreadPoolExecutor extends DynamicThreadPoolExecutor {

	/**
	 * Creates a dynamic scheduled thread pool executor
	 * @param coreSize Amount of threads that will be kept alive even if no more tasks are available
	 * @param maxSize Maximum of threads that can run simultaneously (zero or negative for no limit) 
	 * @param keepAlive Time how long threads should wait for new tasks before they get destructed
	 * @param timeUnit Time unit for waiting for new tasks
	 */
	public DynamicScheduledThreadPoolExecutor(int coreSize, int maxSize, long keepAlive, TimeUnit timeUnit) {
		super(coreSize, maxSize, keepAlive, timeUnit);
		setCorePoolSize(coreSize);
		this.tasks = new ScheduledLinkedBlockingQueue<>();
	}
	
	@Override
	public void setCorePoolSize(int coreSize) {
		if(coreSize < 1)
			throw new IllegalArgumentException("Scheduled thread pools need a core pool size of at least one but "+coreSize+" was given");
		this.coreSize = coreSize;
	}
	
	/**
	 * Returns the total amount of tasks that are still pending, 
	 * including future tasks that are not ready for execution yet
	 * @return Total amount of tasks 
	 */
	public long getPendingFutureTasks() {
		return ((ScheduledBlockingQueue<Runnable>)this.tasks).getTotalSize();
	}

	/**
	 * Executes a given task after a certain duration
	 * @param command Task that should be executed
	 * @param duration Milliseconds after which the task should be executed
	 */
	public void executeIn(Runnable command, long duration) {
		if(command == null) throw new NullPointerException("Runnable cannot be null");
		((ScheduledBlockingQueue<Runnable>)this.tasks).addIn(command, duration);
		updateThreadPool();
	}
	
	/**
	 * Executes a task after a certain time. 
	 * If high utilization it is possible that task 
	 * will not get executed at exact time point
	 * @param command Task that should be executed
	 * @param time System time in milliseconds when task should be executed
	 */
	public void executeAt(Runnable command, long time) {
		if(command == null) throw new NullPointerException("Runnable cannot be null");
		((ScheduledBlockingQueue<Runnable>)this.tasks).addAt(command, time);
		updateThreadPool();
	}
}
