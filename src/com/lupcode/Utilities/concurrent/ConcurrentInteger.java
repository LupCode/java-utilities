package com.lupcode.Utilities.concurrent;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * Same behavior as {@link AtomicInteger} but additionally allows 
 * threads to wait until certain values get reached
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 */
public class ConcurrentInteger extends Number implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected AtomicInteger value = new AtomicInteger();

	/**
     * Creates a new ConcurrentInteger with initial value {@code 0}.
     */
    public ConcurrentInteger() {
    	
    }
	
	/**
     * Creates a new ConcurrentInteger with the given initial value.
     * @param initialValue the initial value
     */
    public ConcurrentInteger(int initialValue) {
    	this.value.set(initialValue);
    }

    /**
     * Gets the current value.
     * @return the current value
     */
    public int get() {
        return value.get();
    }

    /**
     * Sets to the given value.
     * @param newValue the new value
     */
    public synchronized void set(int newValue) {
    	this.value.set(newValue);
    	notifyAll();
    }

    /**
     * Atomically sets to the given value and returns the old value.
     * @param newValue the new value
     * @return the previous value
     */
    public synchronized int getAndSet(int newValue) {
        int v = this.value.getAndSet(newValue);
        notifyAll();
        return v;
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public synchronized boolean compareAndSet(int expect, int update) {
        boolean v = this.value.compareAndSet(expect, update);
        notifyAll();
        return v;
    }

    /**
     * Atomically increments by one the current value.
     * @return the previous value
     */
    public synchronized int getAndIncrement() {
    	int v = this.value.getAndIncrement();
    	notifyAll();
    	return v;
    }

    /**
     * Atomically decrements by one the current value.
     * @return the previous value
     */
    public synchronized int getAndDecrement() {
    	int v = this.value.getAndDecrement();
    	notifyAll();
    	return v;
    }

    /**
     * Atomically adds the given value to the current value.
     * @param delta the value to add
     * @return the previous value
     */
    public synchronized int getAndAdd(int delta) {
    	int v = this.value.getAndAdd(delta);
    	notifyAll();
    	return v;
    }

    /**
     * Atomically increments by one the current value.
     * @return the updated value
     */
    public synchronized int incrementAndGet() {
        int v = this.value.incrementAndGet();
        notifyAll();
        return v;
    }

    /**
     * Atomically decrements by one the current value.
     * @return the updated value
     */
    public synchronized int decrementAndGet() {
        int v = this.value.decrementAndGet();
        notifyAll();
        return v;
    }

    /**
     * Atomically adds the given value to the current value.
     * @param delta the value to add
     * @return the updated value
     */
    public synchronized int addAndGet(int delta) {
    	int v = this.value.addAndGet(delta);
    	notifyAll();
    	return v;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the previous value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     * @param updateFunction a side-effect-free function
     * @return the previous value
     * @since 1.8
     */
    public synchronized int getAndUpdate(IntUnaryOperator updateFunction) {
        int v = this.value.getAndUpdate(updateFunction);
        notifyAll();
        return v;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the updated value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     * @param updateFunction a side-effect-free function
     * @return the updated value
     * @since 1.8
     */
    public synchronized int updateAndGet(IntUnaryOperator updateFunction) {
        int v = this.value.updateAndGet(updateFunction);
        notifyAll();
        return v;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function to the current and given values,
     * returning the previous value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function
     * is applied with the current value as its first argument,
     * and the given update as the second argument.
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the previous value
     * @since 1.8
     */
    public synchronized int getAndAccumulate(int x, IntBinaryOperator accumulatorFunction) {
    	int v = this.value.getAndAccumulate(x, accumulatorFunction);
    	notifyAll();
    	return v;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function to the current and given values,
     * returning the updated value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function
     * is applied with the current value as its first argument,
     * and the given update as the second argument.
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the updated value
     * @since 1.8
     */
    public synchronized int accumulateAndGet(int x, IntBinaryOperator accumulatorFunction) {
    	int v = this.value.accumulateAndGet(x, accumulatorFunction);
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

    @Override
    public int intValue() {
        return get();
    }

    @Override
    public long longValue() {
        return (long)get();
    }

    @Override
    public float floatValue() {
        return (float)get();
    }

    @Override
    public double doubleValue() {
        return (double)get();
    }
    

    
    /**
     * Waits until this value equals zero
     */
    public void awaitZero() {
    	await(i -> i==0);
    }
    
    /**
     * Waits until this value equals zero 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitZero(long timeout) {
    	return await(i -> i==0, timeout);
    }
    
    /**
     * Waits until this value equals zero
     */
    public void awaitNotZero() {
    	await(i -> i!=0);
    }
    
    /**
     * Waits until this value equals zero 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitNotZero(long timeout) {
    	return await(i -> i!=0, timeout);
    }
    
    /**
     * Waits until this value equals the given value
     * @param value Value for checking
     */
    public void awaitEquals(int value) {
    	await(i -> i==value);
    }
    
    /**
     * Waits until this value equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitEquals(int value, long timeout) {
    	return await(i -> i==value, timeout);
    }
    
    /**
     * Waits until this value does not equal the given value
     * @param value Value for checking
     */
    public void awaitNotEquals(int value) {
    	await(i -> i!=value);
    }
    
    /**
     * Waits until this value does not equal the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitNotEquals(int value, long timeout) {
    	return await(i -> i!=value, timeout);
    }
    
    /**
     * Waits until this value is smaller or equals the given value
     * @param value Value for checking
     */
    public void awaitSmallerEquals(int value) {
    	await(i -> i<=value);
    }
    
    /**
     * Waits until this value is smaller or equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitSmallerEquals(int value, long timeout) {
    	return await(i -> i<=value, timeout);
    }
    
    /**
     * Waits until this value is greater or equals the given value
     * @param value Value for checking
     */
    public void awaitGreaterEquals(int value) {
    	await(i -> i>=value);
    }
    
    /**
     * Waits until this value is greater or equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitGreaterEquals(int value, long timeout) {
    	return await(i -> i>=value, timeout);
    }
    
    /**
     * Waits until this value is smaller than the given value
     * @param value Value for checking
     */
    public void awaitSmaller(int value) {
    	await(i -> i<value);
    }
    
    /**
     * Waits until this value is smaller than the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitSmaller(int value, long timeout) {
    	return await(i -> i<value, timeout);
    }
    
    /**
     * Waits until this value is greater than the given value
     * @param value Value for checking
     */
    public void awaitGreater(int value) {
    	await(i -> i>value);
    }
    
    /**
     * Waits until this value is greater than the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitGreater(int value, long timeout) {
    	return await(i -> i>value, timeout);
    }
    
    /**
     * Waits until this value has changed
     */
    public void awaitChange() {
    	final int v = get();
    	await(i -> i!=v);
    }
    
    /**
     * Waits until this value has changed
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public int awaitChange(long timeout) {
    	final int v = get();
    	return await(i -> i!=v, timeout);
    }
    
    /**
     * Waits until the given function returns true for the current value
     * @param func Function that repeatedly gets called with current value 
     * and must return true in order to finish waiting
     * @throws NullPointerException if the function is null
     */
    public synchronized void await(Function<Integer, Boolean> func) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	while(!func.apply(get()))
    		try { wait(); } catch (Exception ex) { ex.printStackTrace(); }
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
    public synchronized int await(Function<Integer, Boolean> func, long timeout) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	long limit = System.currentTimeMillis()+timeout, budget;
    	while((budget = limit-System.currentTimeMillis()) > 0 && !func.apply(get())) {
    		try { wait(budget); } catch (Exception ex) { ex.printStackTrace(); }
    	} return get();
    }
}
