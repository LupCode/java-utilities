package com.lupcode.Utilities.bitmasks;

import java.io.Serializable;

/** Abstract class every bit mask must implement
 * 
 * @author LupCode.com (Luca Vogels)
 * @since 2019-03-04
 * @param <T> must be the class that extends this {@link BitMask} and is used for return types of the methods
 */
public abstract class BitMask<T> implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Sets a {@link Boolean} value for a given bit
	 * @param bit index of the bit that should be set
	 * @param value the bit should be set to
	 * @return this instance
	 * @throws IndexOutOfBoundsException if bit index is out of bounds
	 */
	public abstract T set(int bit, boolean value) throws IndexOutOfBoundsException;
	
	/** Sets all bits to the given value
	 * @param value the bits should be set to
	 * @return this instance
	 */
	public T setAll(boolean value){
		return setAll(0, Integer.MAX_VALUE, value);
	}
	
	/** Sets all bits in a given range to the given value
	 * @param length how many bits should be set (gets shorted if out of bounds)
	 * @param value the bits should be set to
	 * @return this instance
	 */
	public T setAll(int length, boolean value){
		return setAll(0, length, value);
	}
	
	/** Sets all bits in a given range to the given value
	 * @param offset index where to start setting bits (inclusive)
	 * @param length how many bits should be set (gets shorted if out of bounds)
	 * @param value the bits should be set to
	 * @return this instance
	 * @throws IndexOutOfBoundsException if offset if out of bounds
	 */
	public abstract T setAll(int offset, int length, boolean value) throws IndexOutOfBoundsException;
	
	/** Sets a set of bits with given values
	 * @param values that should be set (can be null or empty)
	 * @return this instance
	 */
	public T setAll(boolean[] values){
		return setAll(0, values, 0, values!=null?values.length:0);
	}
	
	/** Sets a set of bits with given values
	 * @param values that should be set (can be null or empty)
	 * @param length how many bits of the values should be read
	 * @return this instance
	 */
	public T setAll(boolean[] values, int length){
		return setAll(0, values, 0, length);
	}
	
	/** Sets a set of bits with given values
	 * @param values that should be set (can be null or empty)
	 * @param offset for the values where to start reading
	 * @param length how many bits of the values should be read
	 * @return this instance
	 */
	public T setAll(boolean[] values, int offset, int length){
		return setAll(0, values, offset, length);
	}
	
	/** Sets a set of bits with given values
	 * @param set_offset where to start setting bits
	 * @param values that should be set (can be null or empty)
	 * @return this instance
	 * @throws IndexOutOfBoundsException if set_offset is out of bounds with bits number of this {@link BitMask}
	 */
	public T setAll(int set_offset, boolean[] values) throws IndexOutOfBoundsException {
		return setAll(set_offset, values, 0, Integer.MAX_VALUE);
	}
	
	/** Sets a set of bits with given values
	 * @param set_offset where to start setting bits
	 * @param values that should be set (can be null or empty)
	 * @param value_length how many bits of the values should be read
	 * @return this instance
	 * @throws IndexOutOfBoundsException if set_offset is out of bounds with bits number of this {@link BitMask}
	 */
	public T setAll(int set_offset, boolean[] values, int value_length) throws IndexOutOfBoundsException {
		return setAll(set_offset, values, 0, value_length);
	}
	
	/** Sets a set of bits with given values
	 * @param set_offset where to start setting bits
	 * @param values that should be set (can be null or empty)
	 * @param value_offset for the values where to start reading
	 * @param value_length how many bits of the values should be read
	 * @return this instance
	 * @throws IndexOutOfBoundsException if set_offset is out of bounds with bits number of this {@link BitMask}
	 */
	public abstract T setAll(int set_offset, boolean[] values, int value_offset, int value_length) throws IndexOutOfBoundsException;
	
	
	
	/** Return the {@link Boolean} value from a given bit
	 * @param bit index of the bit that should be returned
	 * @return value of the bit
	 * @throws IndexOutOfBoundsException if bit index is out of bounds
	 */
	public abstract boolean get(int bit) throws IndexOutOfBoundsException;
	
	
	/** Returns all bit values as {@link Boolean} values
	 * @return bit values
	 */
	public boolean[] getAll(){
		return getAll(0, Integer.MAX_VALUE);
	}
	
	/** Returns a set of {@link Boolean} values from this mask
	 * @param length how many bits should be returned (gets shorted if out of bounds)
	 * @return bit values
	 */
	public boolean[] getAll(int length){
		return getAll(0, length);
	}
	
	/** Returns a set of {@link Boolean} values from this mask
	 * @param offset for the bits that should be returned
	 * @param length how many bits should be returned (gets shorted if out of bounds)
	 * @return bit values
	 * @throws IndexOutOfBoundsException if offset is out of bounds
	 */
	public abstract boolean[] getAll(int offset, int length) throws IndexOutOfBoundsException;
	
	
	
	/** Toggles the value of the given bit
	 * @param bit index of the bit that should be toggled
	 * @return this instance
	 * @throws IndexOutOfBoundsException if bit index is out of bounds
	 */
	public abstract T toggle(int bit) throws IndexOutOfBoundsException;
	
	/** Toggles all bits
	 * @return this instance
	 */
	public T toggleAll(){
		return toggleAll(0, Integer.MAX_VALUE);
	}
	
	/** Toggles all bits in a given range
	 * @param length how many bits should be toggled (gets shorted if out of bounds)
	 * @return this instance
	 */
	public T toggleAll(int length){
		return toggleAll(0, length);
	}
	
	/** Toggles all bits in a given range
	 * @param offset index where to start toggling bits (inclusive)
	 * @param length how many bits should be toggled (gets shorted if out of bounds)
	 * @return this instance
	 * @throws IndexOutOfBoundsException if offset if out of bounds
	 */
	public abstract T toggleAll(int offset, int length) throws IndexOutOfBoundsException;
	
	
	/** Negates this bit mask. Does the same as toggleAll()
	 * @return this instance
	 */
	public T not(){
		return toggleAll();
	}
	
	/** Applies the binary operation AND to this values with the values of another {@link BitMask}
	 * @param other bit mask
	 * @return this instance
	 */
	public T and(BitMask<?> other){
		if(other==null){ throw new NullPointerException("Other cannot be null"); }
		boolean[] a = getAll(), b = other.getAll();
		for(int i=0; i<a.length; i++){
			a[i] = (a[i] && i<b.length && b[i]);
		} return setAll(a);
	}
	
	/** Applies the binary operation NAND to this values with the values of another {@link BitMask}
	 * @param other bit mask
	 * @return this instance
	 */
	public T nand(BitMask<?> other){
		if(other==null){ throw new NullPointerException("Other cannot be null"); }
		boolean[] a = getAll(), b = other.getAll();
		for(int i=0; i<a.length; i++){
			a[i] = !(a[i] && i<b.length && b[i]);
		} return setAll(a);
	}
	
	/** Applies the binary operation OR to this values with the values of another {@link BitMask}
	 * @param other bit mask
	 * @return this instance
	 */
	public T or(BitMask<?> other){
		if(other==null){ throw new NullPointerException("Other cannot be null"); }
		boolean[] a = getAll(), b = other.getAll();
		for(int i=0; i<a.length; i++){
			a[i] = (a[i] || (i<b.length && b[i]));
		} return setAll(a);
	}
	
	/** Applies the binary operation NOR to this values with the values of another {@link BitMask}
	 * @param other bit mask
	 * @return this instance
	 */
	public T nor(BitMask<?> other){
		if(other==null){ throw new NullPointerException("Other cannot be null"); }
		boolean[] a = getAll(), b = other.getAll();
		for(int i=0; i<a.length; i++){
			a[i] = !(a[i] || (i<b.length && b[i]));
		} return setAll(a);
	}
	
	/** Applies the binary operation XOR to this values with the values of another {@link BitMask}
	 * @param other bit mask
	 * @return this instance
	 */
	public T xor(BitMask<?> other){
		if(other==null){ throw new NullPointerException("Other cannot be null"); }
		boolean[] a = getAll(), b = other.getAll();
		for(int i=0; i<a.length; i++){
			a[i] = (a[i] ^ (i<b.length && b[i]));
		} return setAll(a);
	}
	
	/** Applies the binary operation XNOR to this values with the values of another {@link BitMask}
	 * @param other bit mask
	 * @return this instance
	 */
	public T xnor(BitMask<?> other){
		if(other==null){ throw new NullPointerException("Other cannot be null"); }
		boolean[] a = getAll(), b = other.getAll();
		for(int i=0; i<a.length; i++){
			a[i] = (a[i] == (i<b.length && b[i]));
		} return setAll(a);
	}
	
	/**
	 * @return values as binary string (0,1)
	 */
	public abstract String toBinaryString();
	
	@Override
	protected abstract Object clone();
	
	@Override
	public abstract String toString();
	
	
	
	protected long getFullBits(int length){
		long x = 2, res = 1;
		while(length > 0){
			if(length%2==1){ res *= x; }
			length /= 2;
			x *= x;
		} return res - 1;
	}
	
	protected long getBits(boolean[] values, int offset, int length){
		long res = 0;
		for(int i=length+offset; i>offset; i--){
			res = (res << 1) | (values[i-1] ? 1 : 0);
		} return res;
	}
	
	protected boolean[] getBits(long value, int offset, int length){
		boolean[] res = new boolean[length];
		for(int i=0; i<length; i++){
			res[i] = ((value >> (i+offset)) & 1) == 1;
		} return res;
	}
	
	protected String toBinary(long value, int length){
		StringBuilder sb = new StringBuilder();
		for(int i=length - 1; i>-1; i--){
			sb.append( (((value >> i) & 1) == 1) ? "1" : "0");
		} return sb.toString();
	}
}
