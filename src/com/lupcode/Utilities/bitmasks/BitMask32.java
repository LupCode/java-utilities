package com.lupcode.Utilities.bitmasks;

import java.io.Serializable;

/** BitMask that can hold 32x Bits (32x {@link Boolean} values)
 * 
 * @author LupCode.com (Luca Vogels)
 * @since 2019-03-04
 */
public class BitMask32 extends BitMask<BitMask32> implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int SIZE = Integer.SIZE;
	
	private int v=0;
	
	/** New instance with every bit set to zero (false) */
	public BitMask32(){
		
	}
	
	/** New instance with given bits
	 * @param bitmask the bits should be set to
	 */
	public BitMask32(int bitmask){
		v = bitmask;
	}
	
	/** New instance with all bits set to the given value
	 * @param value the bits should be set to
	 */
	public BitMask32(boolean value){
		v = (int) (value ? -1 : 0);
	}
	
	/** New instance with all bits set to the given values
	 * @param values the bits should be set to
	 */
	public BitMask32(boolean[] values){
		setAll(values);
	}
	
	
	/** Sets the bits for this {@link BitMask32}
	 * @param bitmask the values should be set to
	 * @return this instance
	 */
	public BitMask32 setValue(int bitmask){
		this.v = bitmask; return this;
	}
	
	/**
	 * @return the value of the bits of this {@link BitMask32}
	 */
	public int getValue(){
		return v;
	}
	

	@Override
	public BitMask32 set(int bit, boolean value) throws IndexOutOfBoundsException {
		if(bit<0||bit>=SIZE){ throw new IndexOutOfBoundsException("Index "+bit+" out of bounds (0-"+(SIZE-1)+")"); }
		v = (int) (value ? v | (1 << bit) : v & ~(1 << bit));
		return this;
	}

	@Override
	public BitMask32 setAll(int offset, int length, boolean value) throws IndexOutOfBoundsException {
		if(offset<0||offset>=SIZE){ throw new IndexOutOfBoundsException("Offset "+offset+" out of bounds (0-"+(SIZE-1)+")"); }
		length = Math.max(0, Math.min(length, 8-offset));
		int m = (int) (getFullBits(length) << offset);
		v = (int) (value ? v | m : v & ~m);
		return this;
	}

	@Override
	public BitMask32 setAll(int set_offset, boolean[] values, int value_offset, int value_length) throws IndexOutOfBoundsException {
		if(set_offset<0||set_offset>=SIZE){ throw new IndexOutOfBoundsException("SetOffset "+set_offset+" out of bounds (0-"+(SIZE-1)+")"); }
		if(values!=null&&values.length>0){
			value_offset = Math.max(0, Math.min(value_offset, values.length));
			value_length = Math.max(0, Math.min(value_length, values.length-value_offset));
			if(value_length>0){
				v = (int) (getBits(values, value_offset, value_length) << set_offset);
			}
		}
		return this;
	}

	@Override
	public boolean get(int bit) throws IndexOutOfBoundsException {
		if(bit<0||bit>=SIZE){ throw new IndexOutOfBoundsException("Index "+bit+" out of bounds (0-"+(SIZE-1)+")"); }
		return (v & (1 << bit))==1;
	}

	@Override
	public boolean[] getAll(int offset, int length) throws IndexOutOfBoundsException {
		if(offset<0||offset>=SIZE){ throw new IndexOutOfBoundsException("Offset "+offset+" out of bounds (0-"+(SIZE-1)+")"); }
		length = Math.max(0, Math.min(length, 8-offset));
		return getBits(v, offset, length);
	}

	@Override
	public BitMask32 toggle(int bit) throws IndexOutOfBoundsException {
		if(bit<0||bit>=SIZE){ throw new IndexOutOfBoundsException("Index "+bit+" out of bounds (0-"+(SIZE-1)+")"); }
		v ^= 1 << bit;
		return this;
	}

	@Override
	public BitMask32 toggleAll(int offset, int length) throws IndexOutOfBoundsException {
		v ^= getFullBits(length) << offset;
		return this;
	}

	@Override
	public String toBinaryString() {
		return toBinary(v, SIZE);
	}
	
	@Override
	protected BitMask32 clone() {
		return new BitMask32(v);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append("{v=").append(v).append("; b=").append(toBinaryString()).append("}").toString();
	}
}
