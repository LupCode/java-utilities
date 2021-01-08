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
		this.tasks = new ScheduledLinkedBlockingQueue<>();
	}
	
	@Override
	public void setCorePoolSize(int coreSize) {
		this.coreSize = coreSize;
	}
	
	/**
	 * Returns true if there are any tasks 
	 * currently waiting to being processed 
	 * or waiting to being processed in a future 
	 * point in time
	 * @return True if tasks are waiting (including future)
	 */
	public boolean hasPendingFutureTasks() {
		return !((ScheduledBlockingQueue<Runnable>)this.tasks).isCompletelyEmpty();
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
	 * @param duration Milliseconds after which the task should be executed
	 * @param command Task that should be executed
	 */
	public synchronized void executeIn(long duration, Runnable command) {
		if(command == null) throw new NullPointerException("Runnable cannot be null");
		if(shutdown) throw new IllegalStateException(getClass().getSimpleName()+" is currently shutting down");
		((ScheduledBlockingQueue<Runnable>)this.tasks).addIn(duration, command);
		updateThreadPool();
	}
	
	/**
	 * Executes a task after a certain time. 
	 * If high utilization it is possible that task 
	 * will not get executed at exact time point
	 * @param time System time in milliseconds when task should be executed
	 * @param command Task that should be executed
	 */
	public synchronized void executeAt(long time, Runnable command) {
		if(command == null) throw new NullPointerException("Runnable cannot be null");
		if(shutdown) throw new IllegalStateException(getClass().getSimpleName()+" is currently shutting down");
		((ScheduledBlockingQueue<Runnable>)this.tasks).addAt(time, command);
		updateThreadPool();
	}
	
	@Override
	protected void updateThreadPool() {
		if(free.get() > 0 || (maxSize > 0 && threads.size() >= maxSize)) return;
		Thread thread = new Thread(new Runnable() { public void run() {
			boolean incremented = false;
			do {
				if(!incremented) { incremented=true; free.incrementAndGet(); }
				try {
					Runnable task = tasks.poll(keepAlive, timeUnit);
					if(task != null) {
						if(incremented) { incremented=false; free.decrementAndGet(); }
						updateThreadPool();
						try {
							task.run();
						} catch (Exception ex) { ex.printStackTrace(); }
					}
				} catch (InterruptedException e) {}
			} while(!((ScheduledBlockingQueue<Runnable>)tasks).isCompletelyEmpty() || (!shutdown && threads.size() <= coreSize));
			if(incremented) { incremented=false; free.decrementAndGet(); }
			threadsLock.lock();
			threads.remove(Thread.currentThread());
			threadsLock.unlock();
		} });
		threadsLock.lock();
		threads.add(thread);
		threadsLock.unlock();
		thread.start();
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append("{coreSize=").append(coreSize).
				append("; maxSize=").append(maxSize).append("; threads=").append(threads.size()).
				append(" (active=").append(getActiveCount()).append(" free=").append(free.get()).
				append("); pendingTasks=").append(tasks.size()).append(" (withFuture=").
				append(getPendingFutureTasks()).append(")").append("}").toString();
	}
}
