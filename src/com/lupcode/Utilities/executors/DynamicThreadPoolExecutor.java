package com.lupcode.Utilities.executors;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Executes tasks by using multiple threads that are lazily initialized.
 * A core amount of threads keeps running while additional threads get destructed 
 * after a certain period without work to do
 * @author LupCode.com (Luca Vogels)
 * @since 2020-01-07
 */
public class DynamicThreadPoolExecutor implements Executor {

	
	protected AtomicInteger free = new AtomicInteger(0);
	protected BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	protected Lock threadsLock = new ReentrantLock();
	protected ArrayList<Thread> threads = new ArrayList<Thread>();
	
	protected int coreSize, maxSize;
	protected long keepAlive;
	protected TimeUnit timeUnit;
	protected boolean shutdown = false;
	
	/**
	 * Creates a dynamic thread pool executor
	 * @param coreSize Amount of threads that will be kept alive even if no more tasks are available
	 * @param maxSize Maximum of threads that can run simultaneously (zero or negative for no limit) 
	 * @param keepAlive Time how long threads should wait for new tasks before they get destructed
	 * @param timeUnit Time unit for waiting for new tasks
	 */
	public DynamicThreadPoolExecutor(int coreSize, int maxSize, long keepAlive, TimeUnit timeUnit) {
		this.coreSize = coreSize;
		this.maxSize = maxSize;
		this.keepAlive = Math.max(0, keepAlive);
		this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Returns how many tasks are waiting to being processed
	 * @return Amount of waiting tasks
	 */
	public int getPendingTasks() {
		return tasks.size();
	}
	
	/**
	 * Removes the tasks from the waiting queue so it will 
	 * not get executed. Only works if tasks is not already 
	 * being processed by a thread
	 * @param task Task that should be removed
	 * @return True on success
	 */
	public synchronized boolean removeTask(Runnable task) {
		return tasks.remove(task);
	}
	
	/**
	 * Removes all pending tasks so the do not get executed
	 */
	public synchronized void removeAllWaitingTasks() {
		tasks.clear();
	}
	
	/**
	 * Returns how many threads are currently in the pool
	 * @return Amount of threads in pool
	 */
	public int getPoolSize() {
		return threads.size();
	}
	
	/**
	 * Returns how many threads are currently in use
	 * @return Amount of threads in use
	 */
	public int getActiveCount() {
		return threads.size() - free.get();
	}
	
	/**
	 * Returns how many threads are currently waiting for tasks
	 * @return Amount of threads waiting
	 */
	public int getWaitingCount() {
		return free.get();
	}
	
	/**
	 * Minimum amount of threads that stay alive even if no more tasks are scheduled
	 * @return Core amount of threads that stay alive
	 */
	public int getCorePoolSize() {
		return coreSize;
	}
	
	/**
	 * Sets how many threads will stay alive even after no more tasks are available
	 * @param coreSize Amount of threads always alive
	 */
	public void setCorePoolSize(int coreSize) {
		this.coreSize = coreSize;
	}
	
	/**
	 * Returns maximum amount of threads that can run simultaneously. 
	 * If zero or negative no limit is set
	 * @return Maximum amount of threads (zero or negative for no limit)
	 */
	public int getMaximumPoolSize() {
		return maxSize;
	}
	
	/**
	 * Sets maximum amount of threads that can run simultaneously. 
	 * If zero or negative no limit is set
	 * @param maxSize Maximum amount of threads (zero or negative for no limit)
	 */
	public void setMaximumPoolSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	/**
	 * Returns how long threads wait for new tasks before they get destructed
	 * @return Time waiting for tasks before destructed
	 */
	public long getKeepAliveTime() {
		return keepAlive;
	}
	
	/**
	 * Returns the time unit how long threads wait for new tasks
	 * @return Time unit used for waiting
	 */
	public TimeUnit getKeepAliveUnit() {
		return timeUnit;
	}
	
	/**
	 * Sets how long a thread should wait for new tasks before it gets destructed
	 * @param keepAlive Time how long thread should wait
	 * @param timeUnit Time unit of the given keep alive time
	 */
	public void setKeepAliveTime(long keepAlive, TimeUnit timeUnit) {
		this.keepAlive = Math.max(0, keepAlive);
		this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
	}
	
	
	
	@Override
	public synchronized void execute(Runnable command) {
		if(command == null) throw new NullPointerException("Runnable cannot be null");
		if(shutdown) throw new IllegalStateException(getClass().getSimpleName()+" is currently shutting down");
		tasks.add(command);
		updateThreadPool();
	}
	
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
						try {
							task.run();
						} catch (Exception ex) { ex.printStackTrace(); }
					}
				} catch (InterruptedException e) {}
			} while(!tasks.isEmpty() || (!shutdown && threads.size() <= coreSize));
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
	
	/**
	 * Shuts this pool down so no further tasks will be accepted and waits 
	 * until all pending tasks have completed. 
	 * Afterwards new tasks can be offered again
	 */
	public synchronized void shutdown() {
		shutdown = true;
		while(!threads.isEmpty()) {
			threadsLock.lock();
			for(Thread thr : threads)
				try { thr.join(); } catch (InterruptedException e) {}
			threadsLock.unlock();
		}
		shutdown = false;
	}
}
