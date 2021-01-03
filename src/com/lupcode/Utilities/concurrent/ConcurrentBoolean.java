package com.lupcode.Utilities.concurrent;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Same behavior as {@link AtomicBoolean} but additionally allows 
 * threads to wait until certain values get reached
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 */
public class ConcurrentBoolean implements Serializable {

	private static final long serialVersionUID = 1L;

	protected AtomicBoolean value = new AtomicBoolean();
	
	/**
     * Creates a new {@code ConcurrentBoolean} with initial value {@code false}.
     */
    public ConcurrentBoolean() {
    	
    }
	
	/**
     * Creates a new {@code ConcurrentBoolean} with the given initial value.
     * @param initialValue the initial value
     */
    public ConcurrentBoolean(boolean initialValue) {
        this.value.set(initialValue);
    }

    /**
     * Returns the current value.
     * @return the current value
     */
    public boolean get() {
        return this.value.get();
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public synchronized boolean compareAndSet(boolean expect, boolean update) {
        boolean v = this.value.compareAndSet(expect, update);
        notifyAll();
        return v;
    }

    /**
     * Unconditionally sets to the given value.
     * @param newValue the new value
     */
    public synchronized void set(boolean newValue) {
        this.value.set(newValue);
        notifyAll();
    }

    /**
     * Atomically sets to the given value and returns the previous value.
     * @param newValue the new value
     * @return the previous value
     */
    public synchronized boolean getAndSet(boolean newValue) {
    	boolean v = this.value.getAndSet(newValue);
    	notifyAll();
    	return v;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return this.value.toString();
    }
    
    
    /**
     * Waits until this value is true
     */
    public void awaitTrue() {
    	await(i -> i==true);
    }
    
    /**
     * Waits until this value is true 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public boolean awaitTrue(long timeout) {
    	return await(i -> i==true, timeout);
    }
    
    /**
     * Waits until this value is false
     */
    public void awaitFalse() {
    	await(i -> i==false);
    }
    
    /**
     * Waits until this value is false 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public boolean awaitFalse(long timeout) {
    	return await(i -> i==false, timeout);
    }
    
    /**
     * Waits until this value has changed
     */
    public void awaitChange() {
    	final boolean v = get();
    	await(i -> i!=v);
    }
    
    /**
     * Waits until this value has changed
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public boolean awaitChange(long timeout) {
    	final boolean v = get();
    	return await(i -> i!=v, timeout);
    }
    
    /**
     * Waits until the given function returns true for the current value
     * @param func Function that repeatedly gets called with current value 
     * and must return true in order to finish waiting
     * @throws NullPointerException if the function is null
     */
    public void await(Function<Boolean, Boolean> func) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	while(!func.apply(get()))
    		try { wait(); } catch (Exception ex) {}
    }
    
    /**
     * Waits until the given function returns true for the current value
     * or the timeout is exceeded
     * @param func Function that repeatedly gets called with current value 
     * and must return true in order to finish waiting
     * @param timeout Timeout in milliseconds
     * @return Current value
     * @throws NullPointerException if the function is null
     */
    public boolean await(Function<Boolean, Boolean> func, long timeout) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	long limit = System.currentTimeMillis()+timeout, budget;
    	while((budget = limit-System.currentTimeMillis()) > 0 && func.apply(get())) {
    		try { wait(budget); } catch (Exception ex) {}
    	} return get();
    }
}
