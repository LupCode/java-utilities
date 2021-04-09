package com.lupcode.Utilities.concurrent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Same behavior as {@link AtomicBoolean} but additionally allows 
 * threads to wait until certain values get reached
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 */
public class ConcurrentBoolean extends Number implements Serializable {

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

    @Override
    public byte byteValue() {
    	return (byte) (value.get() ? 1 : 0);
    }
    
    @Override
	public int intValue() {
    	return value.get() ? 1 : 0;
	}

	@Override
	public long longValue() {
		return value.get() ? 1 : 0;
	}

	@Override
	public float floatValue() {
		return value.get() ? 1 : 0;
	}

	@Override
	public double doubleValue() {
		return value.get() ? 1 : 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return value.equals(new AtomicBoolean(false));
		if(obj instanceof ConcurrentBoolean) return value.equals((((ConcurrentBoolean)obj).value));
		if(obj instanceof AtomicBoolean) return value.equals((AtomicBoolean)obj);
		if(obj instanceof Boolean) return value.equals(new AtomicBoolean((Boolean)obj));
		if(obj instanceof ConcurrentBigInteger) return value.equals(new AtomicBoolean(!((ConcurrentBigInteger)obj).equals(ConcurrentBigInteger.ZERO())));
		if(obj instanceof BigInteger) return value.equals(new AtomicBoolean(!((BigInteger)obj).equals(BigInteger.ZERO)));
		if(obj instanceof ConcurrentBigDecimal) return value.equals(new AtomicBoolean(!((ConcurrentBigDecimal)obj).equals(ConcurrentBigDecimal.ZERO())));
		if(obj instanceof BigDecimal) return value.equals(new AtomicBoolean(!((BigDecimal)obj).equals(BigDecimal.ZERO)));
		if(obj instanceof ConcurrentLong) return value.equals(new AtomicBoolean(((ConcurrentLong)obj).longValue() != 0));
		if(obj instanceof AtomicLong) return value.equals(new AtomicBoolean(((AtomicLong)obj).longValue() != 0));
		if(obj instanceof Long) return value.equals(new AtomicBoolean((Long)obj != 0));
		if(obj instanceof ConcurrentInteger) return value.equals(new AtomicBoolean(((ConcurrentInteger)obj).intValue() != 0));
		if(obj instanceof AtomicInteger) return value.equals(new AtomicBoolean(((AtomicInteger)obj).intValue() != 0));
		if(obj instanceof Integer) return value.equals(new AtomicBoolean((Integer)obj != 0));
		if(obj instanceof Double) return value.equals(new AtomicBoolean((Double)obj != 0));
		if(obj instanceof Float) return value.equals(new AtomicBoolean((Float)obj != 0));
		if(obj instanceof Short) return value.equals(new AtomicBoolean((Short)obj != 0));;
		if(obj instanceof Byte) return value.equals(new AtomicBoolean((Byte)obj != 0));;
		if(obj instanceof Number) return value.equals(new AtomicBoolean(((Number)obj).byteValue() != 0));;
		return this.value.equals(obj);
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
     * @return Current value
     */
    public boolean awaitTrue() {
    	return await(i -> i==true);
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
     * @return Current value
     */
    public boolean awaitFalse() {
    	return await(i -> i==false);
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
     * @return Current value
     */
    public boolean awaitChange() {
    	final boolean v = get();
    	return await(i -> i!=v);
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
     * @return Current value
     * @throws NullPointerException if the function is null
     */
    public synchronized boolean await(Function<Boolean, Boolean> func) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	while(!func.apply(get()))
    		try { wait(); } catch (Exception ex) { ex.printStackTrace(); }
    	return get();
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
    public synchronized boolean await(Function<Boolean, Boolean> func, long timeout) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	long limit = System.currentTimeMillis()+timeout, budget;
    	while((budget = limit-System.currentTimeMillis()) > 0 && !func.apply(get())) {
    		try { wait(budget); } catch (Exception ex) { ex.printStackTrace(); }
    	} return get();
    }
}
