package com.lupcode.Utilities.concurrent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Thread-safe {@link BigDecimal} with additional functionality such as 
 * allowing threads to wait until a certain value gets reached
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-09
 */
public class ConcurrentBigDecimal extends Number implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected BigDecimal value;
	protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ConcurrentBigDecimal() {
		this.value = new BigDecimal(BigInteger.ZERO);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(BigInteger)
	 */
	public ConcurrentBigDecimal(BigInteger value) {
		this.value = new BigDecimal(value);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(char[])
	 */
	public ConcurrentBigDecimal(char[] value) {
		this.value = new BigDecimal(value);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(double)
	 */
	public ConcurrentBigDecimal(double value) {
		this.value = new BigDecimal(value);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(int)
	 */
	public ConcurrentBigDecimal(int value) {
		this.value = new BigDecimal(value);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(long)
	 */
	public ConcurrentBigDecimal(long value) {
		this.value = new BigDecimal(value);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(String)
	 */
	public ConcurrentBigDecimal(String value) {
		this.value = new BigDecimal(value);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(BigInteger, int)
	 */
	public ConcurrentBigDecimal(BigInteger unscaledValue, int scale) {
		this.value = new BigDecimal(unscaledValue, scale);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(BigInteger, MathContext)
	 */
	public ConcurrentBigDecimal(BigInteger value, MathContext context) {
		this.value = new BigDecimal(value, context);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(char[], MathContext)
	 */
	public ConcurrentBigDecimal(char[] value, MathContext context) {
		this.value = new BigDecimal(value, context);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(double, MathContext)
	 */
	public ConcurrentBigDecimal(double value, MathContext context) {
		this.value = new BigDecimal(value, context);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(int, MathContext)
	 */
	public ConcurrentBigDecimal(int value, MathContext context) {
		this.value = new BigDecimal(value, context);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(long, MathContext)
	 */
	public ConcurrentBigDecimal(long value, MathContext context) {
		this.value = new BigDecimal(value, context);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(String, MathContext)
	 */
	public ConcurrentBigDecimal(String value, MathContext context) {
		this.value = new BigDecimal(value, context);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(BigInteger, int, MathContext)
	 */
	public ConcurrentBigDecimal(BigInteger unscaledValue, int scale, MathContext context) {
		this.value = new BigDecimal(unscaledValue, scale, context);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(char[], int, int)
	 */
	public ConcurrentBigDecimal(char[] value, int off, int len) {
		this.value = new BigDecimal(value, off, len);
	}
	
	/**
	 * @see BigDecimal#BigDecimal(char[], int, int, MathContext)
	 */
	public ConcurrentBigDecimal(char[] value, int off, int len, MathContext context) {
		this.value = new BigDecimal(value, off, len, context);
	}
	
	/**
	 * @see BigDecimal#abs()
	 */
	public BigDecimal abs() {
		try {
			lock.readLock().lock();
			return this.value.abs();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#abs(MathContext)
	 */
	public BigDecimal abs(MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.abs(context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#add(BigDecimal)
	 */
	public BigDecimal add(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.add(value);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#add(BigDecimal, MathContext)
	 */
	public BigDecimal add(BigDecimal val, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.add(value, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	
	@Override
	public byte byteValue() {
		try {
			lock.readLock().lock();
			return this.value.byteValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#byteValueExact()
	 */
	public byte byteValueExact() {
		try {
			lock.readLock().lock();
			return this.value.byteValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divide(BigDecimal)
	 */
	public BigDecimal divide(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.divide(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divide(BigDecimal, int)
	 */
	public BigDecimal divide(BigDecimal val, int roundingMode) {
		try {
			lock.readLock().lock();
			return this.value.divide(val, roundingMode);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divide(BigDecimal, MathContext)
	 */
	public BigDecimal divide(BigDecimal val, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.divide(val, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divide(BigDecimal, RoundingMode)
	 */
	public BigDecimal divide(BigDecimal val, RoundingMode roundingMode) {
		try {
			lock.readLock().lock();
			return this.value.divide(val, roundingMode);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divide(BigDecimal, int, int)
	 */
	public BigDecimal divide(BigDecimal val, int scale, int roundingMode) {
		try {
			lock.readLock().lock();
			return this.value.divide(val, scale, roundingMode);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divide(BigDecimal, int, RoundingMode)
	 */
	public BigDecimal divide(BigDecimal val, int scale, RoundingMode roundingMode) {
		try {
			lock.readLock().lock();
			return this.value.divide(val, scale, roundingMode);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divideAndRemainder(BigDecimal)
	 */
	public BigDecimal[] divideAndRemainder(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.divideAndRemainder(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divideAndRemainder(BigDecimal, MathContext)
	 */
	public BigDecimal[] divideAndRemainder(BigDecimal val, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.divideAndRemainder(val, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divideToIntegralValue(BigDecimal)
	 */
	public BigDecimal divideToIntegralValue(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.divideToIntegralValue(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#divideToIntegralValue(BigDecimal, MathContext)
	 */
	public BigDecimal divideToIntegralValue(BigDecimal val, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.divideToIntegralValue(val, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public double doubleValue() {
		try {
			lock.readLock().lock();
			return this.value.doubleValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return value.equals(BigDecimal.ZERO);
		if(obj instanceof ConcurrentBigDecimal) return value.equals(((ConcurrentBigDecimal)obj).value);
		if(obj instanceof BigDecimal) return value.equals(obj);
		if(obj instanceof ConcurrentBigInteger) return value.equals(new BigDecimal(((ConcurrentBigInteger)obj).value));
		if(obj instanceof BigInteger) return value.equals(new BigDecimal((BigInteger)obj));
		if(obj instanceof ConcurrentLong) return value.equals(new BigDecimal(((ConcurrentLong)obj).longValue()));
		if(obj instanceof AtomicLong) return value.equals(new BigDecimal(((AtomicLong)obj).longValue()));
		if(obj instanceof Long) return value.equals(new BigDecimal((Long)obj));
		if(obj instanceof ConcurrentInteger) return value.equals(new BigDecimal(((ConcurrentInteger)obj).intValue()));
		if(obj instanceof AtomicInteger) return value.equals(new BigDecimal(((AtomicInteger)obj).intValue()));
		if(obj instanceof Integer) return value.equals(new BigDecimal((Integer)obj));
		if(obj instanceof Double) return value.equals(new BigDecimal((Double)obj));
		if(obj instanceof Float) return value.equals(new BigDecimal((Float)obj));
		if(obj instanceof Short) return value.equals(new BigDecimal((Short)obj));
		if(obj instanceof Byte) return value.equals(new BigDecimal((Byte)obj));
		if(obj instanceof ConcurrentBoolean) return value.equals(new BigDecimal(((ConcurrentBoolean)obj).intValue()));
		if(obj instanceof AtomicBoolean) return value.equals(new BigDecimal(((AtomicBoolean)obj).get() ? 1 : 0));
		if(obj instanceof Boolean) return value.equals(new BigDecimal(((Boolean)obj) ? 1 : 0));
		if(obj instanceof Number) return value.equals(new BigDecimal(((Number)obj).doubleValue()));
		return this.value.equals(obj);
	}
	
	@Override
	public float floatValue() {
		try {
			lock.readLock().lock();
			return this.value.floatValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int intValue() {
		try {
			lock.readLock().lock();
			return this.value.intValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#intValueExact()
	 */
	public int intValueExact() {
		try {
			lock.readLock().lock();
			return this.value.intValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public long longValue() {
		try {
			lock.readLock().lock();
			return this.value.longValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#longValueExact()
	 */
	public long longValueExact() {
		try {
			lock.readLock().lock();
			return this.value.longValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * @see BigDecimal#max(BigDecimal)
	 */
	public BigDecimal max(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.max(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#min(BigDecimal)
	 */
	public BigDecimal min(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.min(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#movePointLeft(int)
	 */
	public BigDecimal movePointLeft(int n) {
		try {
			lock.readLock().lock();
			return this.value.movePointLeft(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#movePointRight(int)
	 */
	public BigDecimal movePointRight(int n) {
		try {
			lock.readLock().lock();
			return this.value.movePointRight(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#multiply(BigDecimal)
	 */
	public BigDecimal multiply(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.multiply(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#multiply(BigDecimal, MathContext)
	 */
	public BigDecimal multiply(BigDecimal val, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.multiply(val, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#negate()
	 */
	public BigDecimal negate() {
		try {
			lock.readLock().lock();
			return this.value.negate();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#negate(MathContext)
	 */
	public BigDecimal negate(MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.negate(context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#plus()
	 */
	public BigDecimal plus() {
		try {
			lock.readLock().lock();
			return this.value.plus();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#plus(MathContext)
	 */
	public BigDecimal plus(MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.plus(context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#pow(int)
	 */
	public BigDecimal pow(int n) {
		try {
			lock.readLock().lock();
			return this.value.pow(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#pow(int, MathContext)
	 */
	public BigDecimal pow(int n, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.pow(n, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#precision()
	 */
	public int precision() {
		try {
			lock.readLock().lock();
			return this.value.precision();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#remainder(BigDecimal)
	 */
	public BigDecimal remainder(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.remainder(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#remainder(BigDecimal, MathContext)
	 */
	public BigDecimal remainder(BigDecimal val, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.remainder(val, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#round(MathContext)
	 */
	public BigDecimal round(MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.round(context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#scale()
	 */
	public int scale() {
		try {
			lock.readLock().lock();
			return this.value.scale();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#scaleByPowerOfTen(int)
	 */
	public BigDecimal scaleByPowerOfTen(int n) {
		try {
			lock.readLock().lock();
			return this.value.scaleByPowerOfTen(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#setScale(int)
	 */
	public BigDecimal setScale(int newScale) {
		try {
			lock.readLock().lock();
			return this.value.setScale(newScale);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#setScale(int, int)
	 */
	public BigDecimal setScale(int newScale, int roundingMode) {
		try {
			lock.readLock().lock();
			return this.value.setScale(newScale, roundingMode);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#setScale(int, RoundingMode)
	 */
	public BigDecimal setScale(int newScale, RoundingMode roundingMode) {
		try {
			lock.readLock().lock();
			return this.value.setScale(newScale, roundingMode);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public short shortValue() {
		try {
			lock.readLock().lock();
			return this.value.shortValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#shortValueExact()
	 */
	public short shortValueExact() {
		try {
			lock.readLock().lock();
			return this.value.shortValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#signum()
	 */
	public int signum() {
		try {
			lock.readLock().lock();
			return this.value.signum();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#stripTrailingZeros()
	 */
	public BigDecimal stripTrailingZeros() {
		try {
			lock.readLock().lock();
			return this.value.stripTrailingZeros();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#subtract(BigDecimal)
	 */
	public BigDecimal subtract(BigDecimal val) {
		try {
			lock.readLock().lock();
			return this.value.subtract(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#subtract(BigDecimal, MathContext)
	 */
	public BigDecimal subtract(BigDecimal val, MathContext context) {
		try {
			lock.readLock().lock();
			return this.value.subtract(val, context);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#toBigInteger()
	 */
	public BigInteger toBigInteger() {
		try {
			lock.readLock().lock();
			return this.value.toBigInteger();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#toBigIntegerExact()
	 */
	public BigInteger toBigIntegerExact() {
		try {
			lock.readLock().lock();
			return this.value.toBigIntegerExact();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#toEngineeringString()
	 */
	public String toEngineeringString() {
		try {
			lock.readLock().lock();
			return this.value.toEngineeringString();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#toPlainString()
	 */
	public String toPlainString() {
		try {
			lock.readLock().lock();
			return this.value.toPlainString();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#toString()
	 */
	public String toString() {
		try {
			lock.readLock().lock();
			return this.value.toString();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#ulp()
	 */
	public BigDecimal ulp() {
		try {
			lock.readLock().lock();
			return this.value.ulp();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigDecimal#unscaledValue()
	 */
	public BigInteger unscaledValue() {
		try {
			lock.readLock().lock();
			return this.value.unscaledValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	
	
	
	/**
     * Gets the current value.
     * @return the current value
     */
	public BigDecimal get() {
		try {
			lock.readLock().lock();
			return value;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
     * Sets to the given value.
     * @param newValue the new value
     */
	public void set(BigDecimal val) {
		try {
			lock.writeLock().lock();
			this.value = val!=null ? val : BigDecimal.ZERO;
			notifyAll();
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
     * Atomically sets to the given value and returns the old value.
     * @param newValue the new value
     * @return the previous value
     */
    public BigDecimal getAndSet(BigDecimal value) {
    	try {
    		lock.writeLock().lock();
    		BigDecimal old = this.value;
    		this.value = value;
	        notifyAll();
	        return old;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public boolean compareAndSet(BigDecimal expect, BigDecimal update) {
    	try {
    		lock.writeLock().lock();
    		if(value.equals(expect)) {
    			this.value = update!=null ? update : BigDecimal.ZERO;
    			notifyAll();
    			return true;
    		}
	        return false;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically increments by one the current value.
     * @return the previous value
     */
    public BigDecimal getAndIncrement() {
    	try {
    		lock.writeLock().lock();
    		BigDecimal old = this.value;
    		this.value = value.add(BigDecimal.ONE);
	        notifyAll();
	        return old;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically decrements by one the current value.
     * @return the previous value
     */
    public BigDecimal getAndDecrement() {
    	try {
    		lock.writeLock().lock();
    		BigDecimal old = this.value;
    		this.value = value.subtract(BigDecimal.ONE);
	        notifyAll();
	        return old;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically adds the given value to the current value.
     * @param delta the value to add
     * @return the previous value
     */
    public BigDecimal getAndAdd(BigDecimal delta) {
    	try {
    		lock.writeLock().lock();
    		BigDecimal old = this.value;
    		this.value = value.add(delta!=null ? delta : BigDecimal.ZERO);
	        notifyAll();
	        return old;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically increments by one the current value.
     * @return the updated value
     */
    public BigDecimal incrementAndGet() {
    	try {
    		lock.writeLock().lock();
    		this.value = value.add(BigDecimal.ONE);
	        notifyAll();
	        return this.value;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically decrements by one the current value.
     * @return the updated value
     */
    public BigDecimal decrementAndGet() {
    	try {
    		lock.writeLock().lock();
    		this.value = value.subtract(BigDecimal.ONE);
	        notifyAll();
	        return this.value;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically adds the given value to the current value.
     * @param delta the value to add
     * @return the updated value
     */
    public BigDecimal addAndGet(BigDecimal delta) {
    	try {
    		lock.writeLock().lock();
    		this.value = value.add(delta!=null ? delta : BigDecimal.ZERO);
	        notifyAll();
	        return this.value;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the previous value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     * @param updateFunction a side-effect-free function
     * @return the previous value
     */
    public BigDecimal getAndUpdate(UnaryOperator<BigDecimal> updateFunction) {
    	try {
    		lock.writeLock().lock();
    		BigDecimal old = this.value;
    		this.value = updateFunction.apply(this.value);
    		this.value = this.value!=null ? this.value : BigDecimal.ZERO;
	        notifyAll();
	        return old;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the updated value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     * @param updateFunction a side-effect-free function
     * @return the updated value
     */
    public BigDecimal updateAndGet(UnaryOperator<BigDecimal> updateFunction) {
    	try {
    		lock.writeLock().lock();
    		this.value = updateFunction.apply(this.value);
    		this.value = this.value!=null ? this.value : BigDecimal.ZERO;
	        notifyAll();
	        return this.value;
    	} finally {
    		lock.writeLock().unlock();
    	}
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
    public BigDecimal getAndAccumulate(BigDecimal x, BinaryOperator<BigDecimal> accumulatorFunction) {
    	try {
    		lock.writeLock().lock();
    		BigDecimal old = this.value;
    		this.value = accumulatorFunction.apply(this.value, x);
    		this.value = this.value!=null ? this.value : BigDecimal.ZERO;
	        notifyAll();
	        return old;
    	} finally {
    		lock.writeLock().unlock();
    	}
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
    public BigDecimal accumulateAndGet(BigDecimal x, BinaryOperator<BigDecimal> accumulatorFunction) {
    	try {
    		lock.writeLock().lock();
    		this.value = accumulatorFunction.apply(this.value, x);
    		this.value = this.value!=null ? this.value : BigDecimal.ZERO;
	        notifyAll();
	        return this.value;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }
	
	
	
	
	
	/**
     * Waits until this value equals zero
     */
    public BigDecimal awaitZero() {
    	return await(i -> i.equals(BigDecimal.ZERO));
    }
    
    /**
     * Waits until this value equals zero 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitZero(long timeout) {
    	return await(i -> i.equals(BigDecimal.ZERO), timeout);
    }
    
    /**
     * Waits until this value equals zero
     * @return Current value
     */
    public BigDecimal awaitNotZero() {
    	return await(i -> !i.equals(BigDecimal.ZERO));
    }
    
    /**
     * Waits until this value equals zero 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitNotZero(long timeout) {
    	return await(i -> !i.equals(BigDecimal.ZERO), timeout);
    }
    
    /**
     * Waits until this value equals the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigDecimal awaitEquals(BigDecimal value) {
    	return await(i -> i.equals(value!=null ? value : BigDecimal.ZERO));
    }
    
    /**
     * Waits until this value equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitEquals(BigDecimal value, long timeout) {
    	return await(i -> i.equals(value!=null ? value : BigDecimal.ZERO), timeout);
    }
    
    /**
     * Waits until this value does not equal the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigDecimal awaitNotEquals(BigDecimal value) {
    	return await(i -> !i.equals(value!=null ? value : BigDecimal.ZERO));
    }
    
    /**
     * Waits until this value does not equal the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitNotEquals(BigDecimal value, long timeout) {
    	return await(i -> !i.equals(value!=null ? value : BigDecimal.ZERO), timeout);
    }
    
    /**
     * Waits until this value is smaller or equals the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigDecimal awaitSmallerEquals(BigDecimal value) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) <= 0);
    }
    
    /**
     * Waits until this value is smaller or equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitSmallerEquals(BigDecimal value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) <= 0, timeout);
    }
    
    /**
     * Waits until this value is greater or equals the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigDecimal awaitGreaterEquals(BigDecimal value) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) >= 0);
    }
    
    /**
     * Waits until this value is greater or equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitGreaterEquals(BigDecimal value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) >= 0, timeout);
    }
    
    /**
     * Waits until this value is smaller than the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigDecimal awaitSmaller(BigDecimal value) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) < 0);
    }
    
    /**
     * Waits until this value is smaller than the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitSmaller(BigDecimal value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) < 0, timeout);
    }
    
    /**
     * Waits until this value is greater than the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigDecimal awaitGreater(BigDecimal value) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) > 0);
    }
    
    /**
     * Waits until this value is greater than the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitGreater(BigDecimal value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigDecimal.ZERO) > 0, timeout);
    }
    
    /**
     * Waits until this value has changed
     * @return Current value
     */
    public BigDecimal awaitChange() {
    	final BigDecimal v = get();
    	return await(i -> i!=v);
    }
    
    /**
     * Waits until this value has changed
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigDecimal awaitChange(long timeout) {
    	final BigDecimal v = get();
    	return await(i -> i!=v, timeout);
    }
    
    /**
     * Waits until the given function returns true for the current value
     * @param func Function that repeatedly gets called with current value 
     * and must return true in order to finish waiting
     * @return Current value
     * @throws NullPointerException if the function is null
     */
    public BigDecimal await(Function<BigDecimal, Boolean> func) throws NullPointerException {
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
    public BigDecimal await(Function<BigDecimal, Boolean> func, long timeout) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	long limit = System.currentTimeMillis()+timeout, budget;
    	while((budget = limit-System.currentTimeMillis()) > 0 && !func.apply(get())) {
    		try { wait(budget); } catch (Exception ex) { ex.printStackTrace(); }
    	} return get();
    }
	
	
	
	
	
	/**
	 * @see BigDecimal#ZERO
	 */
	public static ConcurrentBigDecimal ZERO() {
		return new ConcurrentBigDecimal(0);
	}
	
	/**
	 * @see BigDecimal#ONE
	 */
	public static ConcurrentBigDecimal ONE() {
		return new ConcurrentBigDecimal(1);
	}
	
	/**
	 * @see BigDecimal#TEN
	 */
	public static ConcurrentBigDecimal TEN() {
		return new ConcurrentBigDecimal(10);
	}
	
	/**
	 * @see BigDecimal#valueOf(double)
	 */
	public static ConcurrentBigDecimal valueOf(double val) {
		return new ConcurrentBigDecimal(val);
	}
	
	/**
	 * @see BigDecimal#valueOf(long)
	 */
	public static ConcurrentBigDecimal valueOf(long val) {
		return new ConcurrentBigDecimal(val);
	}
	
	/**
	 * @see BigDecimal#valueOf(long, int)
	 */
	public static ConcurrentBigDecimal valueOf(long unscaledValue, int scale) {
		return new ConcurrentBigDecimal(BigInteger.valueOf(unscaledValue), scale);
	}
}
