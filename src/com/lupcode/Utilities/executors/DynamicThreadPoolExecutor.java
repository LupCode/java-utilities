package com.lupcode.Utilities.executors;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Executes tasks by using multiple threads that are lazily initialized.
 * A core amount of threads keeps running while additional threads get destructed 
 * after a certain period without work to do
 * @author LupCode.com (Luca Vogels)
 * @since 2020-12-29
 */
public class DynamicThreadPoolExecutor implements Executor {

	
	protected AtomicInteger free = new AtomicInteger(0);
	protected LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
	protected ConcurrentLinkedQueue<Thread> threads = new ConcurrentLinkedQueue<Thread>();
	
	protected int coreSize, maxSize;
	protected long keepAlive;
	protected TimeUnit timeUnit;
	protected boolean shutdown = false;
	
	public DynamicThreadPoolExecutor(int coreSize, int maxSize, long keepAlive, TimeUnit timeUnit) {
		this.coreSize = coreSize;
		this.maxSize = maxSize;
		this.keepAlive = Math.max(0, keepAlive);
		this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
	}
	
	public int getCorePoolSize() {
		return coreSize;
	}
	public void setCorePoolSize(int coreSize) {
		this.coreSize = coreSize;
	}
	
	public int getMaximumPoolSize() {
		return maxSize;
	}
	public void setMaximumPoolSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public long getKeepAliveTime() {
		return keepAlive;
	}
	public void setKeepAliveTime(long keepAlive) {
		this.keepAlive = Math.max(0, keepAlive);
	}
	
	public TimeUnit getKeepAliveUnit() {
		return timeUnit;
	}
	public void setKeepAliveUnit(TimeUnit timeUnit) {
		this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
	}
	
	
	@Override
	public synchronized void execute(Runnable command) {
		if(command == null) throw new NullPointerException("Runnable cannot be null");
		if(shutdown) throw new IllegalStateException(getClass().getSimpleName()+" is currently shutting down");
		tasks.add(command);
		if(free.get() > 0 || (maxSize > 0 && threads.size() >= maxSize)) return;
		Thread thread = new Thread(new Runnable() { public void run() {
			free.incrementAndGet();
			boolean incremented = true;
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
			threads.remove(Thread.currentThread());
		} });
		threads.add(thread);
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
			for(Thread thr : threads)
				try { thr.join(); } catch (InterruptedException e) {}
		}
		shutdown = false;
	}
}
