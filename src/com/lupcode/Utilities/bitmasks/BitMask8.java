package com.lupcode.Utilities.bitmasks;

import java.io.Serializable;

/** BitMask that can hold 8x Bits (8x {@link Boolean} values)
 * 
 * @author LupCode.com (Luca Vogels)
 * @since 2019-03-04
 */
public class BitMask8 extends BitMask<BitMask8> implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int SIZE = Byte.SIZE;
	
	private byte v=0;
	
	/** New instance with every bit set to zero (false) */
	public BitMask8(){
		
	}
	
	/** New instance with given bits
	 * @param bitmask the bits should be set to
	 */
	public BitMask8(byte bitmask){
		v = bitmask;
	}
	
	/** New instance with all bits set to the given value
	 * @param value the bits should be set to
	 */
	public BitMask8(boolean value){
		v = (byte) (value ? -1 : 0);
	}
	
	/** New instance with all bits set to the given values
	 * @param values the bits should be set to
	 */
	public BitMask8(boolean[] values){
		setAll(values);
	}
	
	
	/** Sets the bits for this {@link BitMask8}
	 * @param bitmask the values should be set to
	 * @return this instance
	 */
	public BitMask8 setValue(byte bitmask){
		this.v = bitmask; return this;
	}
	
	/**
	 * @return the value of the bits of this {@link BitMask8}
	 */
	public byte getValue(){
		return v;
	}
	

	@Override
	public BitMask8 set(int bit, boolean value) throws IndexOutOfBoundsException {
		if(bit<0||bit>=SIZE){ throw new IndexOutOfBoundsException("Index "+bit+" out of bounds (0-"+(SIZE-1)+")"); }
		v = (byte) (value ? v | (1 << bit) : v & ~(1 << bit));
		return this;
	}

	@Override
	public BitMask8 setAll(int offset, int length, boolean value) throws IndexOutOfBoundsException {
		if(offset<0||offset>=SIZE){ throw new IndexOutOfBoundsException("Offset "+offset+" out of bounds (0-"+(SIZE-1)+")"); }
		length = Math.max(0, Math.min(length, 8-offset));
		byte m = (byte) (getFullBits(length) << offset);
		v = (byte) (value ? v | m : v & ~m);
		return this;
	}

	@Override
	public BitMask8 setAll(int set_offset, boolean[] values, int value_offset, int value_length) throws IndexOutOfBoundsException {
		if(set_offset<0||set_offset>=SIZE){ throw new IndexOutOfBoundsException("SetOffset "+set_offset+" out of bounds (0-"+(SIZE-1)+")"); }
		if(values!=null&&values.length>0){
			value_offset = Math.max(0, Math.min(value_offset, values.length));
			value_length = Math.max(0, Math.min(value_length, values.length-value_offset));
			if(value_length>0){
				v = (byte) (getBits(values, value_offset, value_length) << set_offset);
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
	public BitMask8 toggle(int bit) throws IndexOutOfBoundsException {
		if(bit<0||bit>=SIZE){ throw new IndexOutOfBoundsException("Index "+bit+" out of bounds (0-"+(SIZE-1)+")"); }
		v ^= 1 << bit;
		return this;
	}

	@Override
	public BitMask8 toggleAll(int offset, int length) throws IndexOutOfBoundsException {
		v ^= getFullBits(length) << offset;
		return this;
	}

	@Override
	public String toBinaryString() {
		return toBinary(v, SIZE);
	}
	
	@Override
	protected BitMask8 clone() {
		return new BitMask8(v);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append("{v=").append(v).append("; b=").append(toBinaryString()).append("}").toString();
	}
}
