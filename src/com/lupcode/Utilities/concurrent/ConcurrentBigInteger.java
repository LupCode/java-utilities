package com.lupcode.Utilities.concurrent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Thread-safe {@link BigInteger} with additional functionality such as 
 * allowing threads to wait until a certain value gets reached
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-08
 */
public class ConcurrentBigInteger extends Number implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	protected BigInteger value;
	protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ConcurrentBigInteger() {
		this.value = BigInteger.ZERO;
	}
	
	public ConcurrentBigInteger(long value) {
		this.value = BigInteger.valueOf(value);
	}
	
	public ConcurrentBigInteger(BigInteger value) {
		this.value = value!=null ? value : BigInteger.ZERO;
	}
	
	/**
	 * 
	 * @see BigInteger#BigInteger(byte[])
	 */
	public ConcurrentBigInteger(byte[] value) {
		this.value = new BigInteger(value);
	}
	/**
	 * 
	 * @see BigInteger#BigInteger(String)
	 */
	public ConcurrentBigInteger(String value) {
		this.value = new BigInteger(value);
	}
	/**
	 * 
	 * @see BigInteger#BigInteger(String, int)
	 */
	public ConcurrentBigInteger(String value, int radix) {
		this.value = new BigInteger(value, radix);
	}
	/**
	 * @see BigInteger#BigInteger(int, byte[]))
	 */
	public ConcurrentBigInteger(int signum, byte[] magnitude) {
		this.value = new BigInteger(signum, magnitude);
	}
	/**
	 * @see BigInteger#BigInteger(int, Random)
	 */
	public ConcurrentBigInteger(int numBits, Random rnd) {
		this.value = new BigInteger(numBits, rnd);
	}
	/**
	 * @see BigInteger#BigInteger(int, int, Random)
	 */
	public ConcurrentBigInteger(int bitLength, int certainty, Random rnd) {
		this.value = new BigInteger(bitLength, certainty, rnd);
	}
	
	/**
	 * @see BigInteger#abs()
	 */
	public BigInteger abs() {
		try {
			lock.readLock().lock();
			return value.abs();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#add(BigInteger)
	 */
	public BigInteger add(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.add(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#and(BigInteger)
	 */
	public BigInteger and(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.and(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#andNot(BigInteger)
	 */
	public BigInteger andNot(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.andNot(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#bitCount()
	 */
	public int bitCount() {
		try {
			lock.readLock().lock();
			return value.bitCount();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#bitLength()
	 */
	public int bitLength() {
		try {
			lock.readLock().lock();
			return value.bitLength();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public byte byteValue() {
		try {
			lock.readLock().lock();
			return value.byteValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#byteValueExact()
	 */
	public byte byteValueExact() {
		try {
			lock.readLock().lock();
			return value.byteValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#clearBit(int)
	 */
	public BigInteger clearBit(int n) {
		try {
			lock.readLock().lock();
			return value.clearBit(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#compareTo(BigInteger)
	 */
	public int compareTo(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.compareTo(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#divide(BigInteger)
	 */
	public BigInteger divide(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.divide(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#divideAndRemainder(BigInteger)
	 */
	public BigInteger[] divideAndRemainder(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.divideAndRemainder(val);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public double doubleValue() {
		try {
			lock.readLock().lock();
			return value.doubleValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return value.equals(BigInteger.ZERO);
		if(obj instanceof ConcurrentBigInteger) return value.equals(((ConcurrentBigInteger)obj).value);
		if(obj instanceof BigInteger) return value.equals((BigInteger)obj);
		if(obj instanceof ConcurrentBigDecimal) return new BigDecimal(value).equals(((ConcurrentBigDecimal)obj).value);
		if(obj instanceof BigDecimal) return new BigDecimal(value).equals(obj);
		if(obj instanceof ConcurrentLong) return value.equals(BigInteger.valueOf(((ConcurrentLong)obj).longValue()));
		if(obj instanceof AtomicLong) return value.equals(BigInteger.valueOf(((AtomicLong)obj).longValue()));
		if(obj instanceof Long) return value.equals(BigInteger.valueOf((Long)obj));
		if(obj instanceof ConcurrentInteger) return value.equals(BigInteger.valueOf(((ConcurrentInteger)obj).intValue()));
		if(obj instanceof AtomicInteger) return value.equals(BigInteger.valueOf(((AtomicInteger)obj).intValue()));
		if(obj instanceof Integer) return value.equals(BigInteger.valueOf((Integer)obj));
		if(obj instanceof Double) return new BigDecimal(value).equals(new BigDecimal((Double)obj));
		if(obj instanceof Float) return new BigDecimal(value).equals(new BigDecimal((Float)obj));
		if(obj instanceof Short) return value.equals(BigInteger.valueOf((Short)obj));
		if(obj instanceof Byte) return value.equals(BigInteger.valueOf((Byte)obj));
		if(obj instanceof ConcurrentBoolean) return value.equals(BigInteger.valueOf(((ConcurrentBoolean)obj).intValue()));
		if(obj instanceof AtomicBoolean) return value.equals(BigInteger.valueOf(((AtomicBoolean)obj).get() ? 1 : 0));
		if(obj instanceof Boolean) return value.equals(BigInteger.valueOf(((Boolean)obj) ? 1 : 0));
		if(obj instanceof Number) return value.equals(BigInteger.valueOf(((Number)obj).longValue()));
		return this.value.equals(obj);
	}
	
	/**
	 * @see BigInteger#flipBit(int)
	 */
	public BigInteger flipBit(int n) {
		try {
			lock.readLock().lock();
			return value.flipBit(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public float floatValue() {
		try {
			lock.readLock().lock();
			return value.floatValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#gcd(BigInteger)
	 */
	public BigInteger gcd(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.gcd(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#getLowestSetBit()
	 */
	public int getLowestSetBit() {
		try {
			lock.readLock().lock();
			return value.getLowestSetBit();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int intValue() {
		try {
			lock.readLock().lock();
			return value.intValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#intValueExact()
	 */
	public int intValueExact() {
		try {
			lock.readLock().lock();
			return value.intValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public long longValue() {
		try {
			lock.readLock().lock();
			return value.longValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#longValueExact()
	 */
	public long longValueExact() {
		try {
			lock.readLock().lock();
			return value.longValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#max(BigInteger)
	 */
	public BigInteger max(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.max(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#min(BigInteger)
	 */
	public BigInteger min(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.min(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#mod(BigInteger)
	 */
	public BigInteger mod(BigInteger m) {
		try {
			lock.readLock().lock();
			return value.mod(m);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#modInverse(BigInteger)
	 */
	public BigInteger modInverse(BigInteger m) {
		try {
			lock.readLock().lock();
			return value.modInverse(m);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#modPow(BigInteger, BigInteger))
	 */
	public BigInteger modPow(BigInteger exponent, BigInteger m) {
		try {
			lock.readLock().lock();
			return value.modPow(exponent, m);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#multiply(BigInteger)
	 */
	public BigInteger multiply(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.multiply(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#negate()
	 */
	public BigInteger negate() {
		try {
			lock.readLock().lock();
			return value.negate();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#nextProbablePrime()
	 */
	public BigInteger nextProbablePrime() {
		try {
			lock.readLock().lock();
			return value.nextProbablePrime();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#not()
	 */
	public BigInteger not() {
		try {
			lock.readLock().lock();
			return value.not();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#or(BigInteger)
	 */
	public BigInteger or(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.or(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#pow(int)
	 */
	public BigInteger pow(int exponent) {
		try {
			lock.readLock().lock();
			return value.pow(exponent);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#remainder(BigInteger)
	 */
	public BigInteger remainder(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.remainder(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#setBit(int)
	 */
	public BigInteger setBit(int n) {
		try {
			lock.readLock().lock();
			return value.setBit(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#shiftLeft(int)
	 */
	public BigInteger shiftLeft(int n) {
		try {
			lock.readLock().lock();
			return value.shiftLeft(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#shiftRight(int)
	 */
	public BigInteger shiftRight(int n) {
		try {
			lock.readLock().lock();
			return value.shiftRight(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public short shortValue() {
		try {
			lock.readLock().lock();
			return value.shortValue();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#shortValueExact()
	 */
	public short shortValueExact() {
		try {
			lock.readLock().lock();
			return value.shortValueExact();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#signum()
	 */
	public int signum() {
		try {
			lock.readLock().lock();
			return value.signum();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#subtract(BigInteger)
	 */
	public BigInteger subtract(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.subtract(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#testBit(int)
	 */
	public boolean testBit(int n) {
		try {
			lock.readLock().lock();
			return value.testBit(n);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see BigInteger#toByteArray()
	 */
	public byte[] toByteArray() {
		try {
			lock.readLock().lock();
			return value.toByteArray();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	/**
	 * @see BigInteger#toString(int)
	 */
	public String toString(int radix) {
		return value.toString(radix);
	}
	
	/**
	 * @see BigInteger#xor(BigInteger)
	 */
	public BigInteger xor(BigInteger val) {
		try {
			lock.readLock().lock();
			return value.xor(val);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	
	
	
	
	/**
     * Gets the current value.
     * @return the current value
     */
	public BigInteger get() {
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
	public void set(BigInteger val) {
		try {
			lock.writeLock().lock();
			this.value = val!=null ? val : BigInteger.ZERO;
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
    public BigInteger getAndSet(BigInteger value) {
    	try {
    		lock.writeLock().lock();
    		BigInteger old = this.value;
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
    public boolean compareAndSet(BigInteger expect, BigInteger update) {
    	try {
    		lock.writeLock().lock();
    		if(value.equals(expect)) {
    			this.value = update!=null ? update : BigInteger.ZERO;
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
    public BigInteger getAndIncrement() {
    	try {
    		lock.writeLock().lock();
    		BigInteger old = this.value;
    		this.value = value.add(BigInteger.ONE);
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
    public BigInteger getAndDecrement() {
    	try {
    		lock.writeLock().lock();
    		BigInteger old = this.value;
    		this.value = value.subtract(BigInteger.ONE);
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
    public BigInteger getAndAdd(BigInteger delta) {
    	try {
    		lock.writeLock().lock();
    		BigInteger old = this.value;
    		this.value = value.add(delta!=null ? delta : BigInteger.ZERO);
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
    public BigInteger incrementAndGet() {
    	try {
    		lock.writeLock().lock();
    		this.value = value.add(BigInteger.ONE);
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
    public BigInteger decrementAndGet() {
    	try {
    		lock.writeLock().lock();
    		this.value = value.subtract(BigInteger.ONE);
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
    public BigInteger addAndGet(BigInteger delta) {
    	try {
    		lock.writeLock().lock();
    		this.value = value.add(delta!=null ? delta : BigInteger.ZERO);
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
    public BigInteger getAndUpdate(UnaryOperator<BigInteger> updateFunction) {
    	try {
    		lock.writeLock().lock();
    		BigInteger old = this.value;
    		this.value = updateFunction.apply(this.value);
    		this.value = this.value!=null ? this.value : BigInteger.ZERO;
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
    public BigInteger updateAndGet(UnaryOperator<BigInteger> updateFunction) {
    	try {
    		lock.writeLock().lock();
    		this.value = updateFunction.apply(this.value);
    		this.value = this.value!=null ? this.value : BigInteger.ZERO;
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
    public BigInteger getAndAccumulate(BigInteger x, BinaryOperator<BigInteger> accumulatorFunction) {
    	try {
    		lock.writeLock().lock();
    		BigInteger old = this.value;
    		this.value = accumulatorFunction.apply(this.value, x);
    		this.value = this.value!=null ? this.value : BigInteger.ZERO;
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
    public BigInteger accumulateAndGet(BigInteger x, BinaryOperator<BigInteger> accumulatorFunction) {
    	try {
    		lock.writeLock().lock();
    		this.value = accumulatorFunction.apply(this.value, x);
    		this.value = this.value!=null ? this.value : BigInteger.ZERO;
	        notifyAll();
	        return this.value;
    	} finally {
    		lock.writeLock().unlock();
    	}
    }
	
    
	
	
	
    /**
     * Waits until this value equals zero
     */
    public BigInteger awaitZero() {
    	return await(i -> i.equals(BigInteger.ZERO));
    }
    
    /**
     * Waits until this value equals zero 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitZero(long timeout) {
    	return await(i -> i.equals(BigInteger.ZERO), timeout);
    }
    
    /**
     * Waits until this value equals zero
     * @return Current value
     */
    public BigInteger awaitNotZero() {
    	return await(i -> !i.equals(BigInteger.ZERO));
    }
    
    /**
     * Waits until this value equals zero 
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitNotZero(long timeout) {
    	return await(i -> !i.equals(BigInteger.ZERO), timeout);
    }
    
    /**
     * Waits until this value equals the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigInteger awaitEquals(BigInteger value) {
    	return await(i -> i.equals(value!=null ? value : BigInteger.ZERO));
    }
    
    /**
     * Waits until this value equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitEquals(BigInteger value, long timeout) {
    	return await(i -> i.equals(value!=null ? value : BigInteger.ZERO), timeout);
    }
    
    /**
     * Waits until this value does not equal the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigInteger awaitNotEquals(BigInteger value) {
    	return await(i -> !i.equals(value!=null ? value : BigInteger.ZERO));
    }
    
    /**
     * Waits until this value does not equal the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitNotEquals(BigInteger value, long timeout) {
    	return await(i -> !i.equals(value!=null ? value : BigInteger.ZERO), timeout);
    }
    
    /**
     * Waits until this value is smaller or equals the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigInteger awaitSmallerEquals(BigInteger value) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) <= 0);
    }
    
    /**
     * Waits until this value is smaller or equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitSmallerEquals(BigInteger value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) <= 0, timeout);
    }
    
    /**
     * Waits until this value is greater or equals the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigInteger awaitGreaterEquals(BigInteger value) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) >= 0);
    }
    
    /**
     * Waits until this value is greater or equals the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitGreaterEquals(BigInteger value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) >= 0, timeout);
    }
    
    /**
     * Waits until this value is smaller than the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigInteger awaitSmaller(BigInteger value) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) < 0);
    }
    
    /**
     * Waits until this value is smaller than the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitSmaller(BigInteger value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) < 0, timeout);
    }
    
    /**
     * Waits until this value is greater than the given value
     * @param value Value for checking
     * @return Current value
     */
    public BigInteger awaitGreater(BigInteger value) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) > 0);
    }
    
    /**
     * Waits until this value is greater than the given value
     * or the timeout is exceeded
     * @param value Value for checking
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitGreater(BigInteger value, long timeout) {
    	return await(i -> i.compareTo(value!=null ? value : BigInteger.ZERO) > 0, timeout);
    }
    
    /**
     * Waits until this value has changed
     * @return Current value
     */
    public BigInteger awaitChange() {
    	final BigInteger v = get();
    	return await(i -> i!=v);
    }
    
    /**
     * Waits until this value has changed
     * or the timeout is exceeded
     * @param timeout Timeout in milliseconds
     * @return Current value
     */
    public BigInteger awaitChange(long timeout) {
    	final BigInteger v = get();
    	return await(i -> i!=v, timeout);
    }
    
    /**
     * Waits until the given function returns true for the current value
     * @param func Function that repeatedly gets called with current value 
     * and must return true in order to finish waiting
     * @return Current value
     * @throws NullPointerException if the function is null
     */
    public BigInteger await(Function<BigInteger, Boolean> func) throws NullPointerException {
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
    public BigInteger await(Function<BigInteger, Boolean> func, long timeout) throws NullPointerException {
    	if(func == null) throw new NullPointerException("Function cannot be null");
    	long limit = System.currentTimeMillis()+timeout, budget;
    	while((budget = limit-System.currentTimeMillis()) > 0 && !func.apply(get())) {
    		try { wait(budget); } catch (Exception ex) { ex.printStackTrace(); }
    	} return get();
    }
	
	
	
	
	
	/**
	 * @see BigInteger#ZERO
	 */
	public static ConcurrentBigInteger ZERO() {
		return new ConcurrentBigInteger(0);
	}
	
	/**
	 * @see BigInteger#ONE
	 */
	public static ConcurrentBigInteger ONE() {
		return new ConcurrentBigInteger(1);
	}
	
	/**
	 * @see BigInteger#TEN
	 */
	public static ConcurrentBigInteger TEN() {
		return new ConcurrentBigInteger(10);
	}
	
	/**
	 * @see BigInteger#valueOf(long)
	 */
	public static ConcurrentBigInteger valueOf(long value) {
		return new ConcurrentBigInteger(value);
	}
	
	/**
	 * @see BigInteger#probablePrime(int, Random)
	 */
	public static ConcurrentBigInteger probablePrime(int bitLength, Random rnd) {
		return new ConcurrentBigInteger(BigInteger.probablePrime(bitLength, rnd));
	}
}
