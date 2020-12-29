package com.lupcode.Utilities.bitmasks;

import java.io.Serializable;

/** BitMask that can hold 16x Bits (16x {@link Boolean} values)
 * 
 * @author LupCode.com (Luca Vogels)
 * @since 2019-03-04
 */
public class BitMask16 extends BitMask<BitMask16> implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int SIZE = Short.SIZE;
	
	private short v=0;
	
	/** New instance with every bit set to zero (false) */
	public BitMask16(){
		
	}
	
	/** New instance with given bits
	 * @param bitmask the bits should be set to
	 */
	public BitMask16(short bitmask){
		v = bitmask;
	}
	
	/** New instance with all bits set to the given value
	 * @param value the bits should be set to
	 */
	public BitMask16(boolean value){
		v = (short) (value ? -1 : 0);
	}
	
	/** New instance with all bits set to the given values
	 * @param values the bits should be set to
	 */
	public BitMask16(boolean[] values){
		setAll(values);
	}
	
	
	/** Sets the bits for this {@link BitMask16}
	 * @param bitmask the values should be set to
	 * @return this instance
	 */
	public BitMask16 setValue(short bitmask){
		this.v = bitmask; return this;
	}
	
	/**
	 * @return the value of the bits of this {@link BitMask16}
	 */
	public short getValue(){
		return v;
	}
	

	@Override
	public BitMask16 set(int bit, boolean value) throws IndexOutOfBoundsException {
		if(bit<0||bit>=SIZE){ throw new IndexOutOfBoundsException("Index "+bit+" out of bounds (0-"+(SIZE-1)+")"); }
		v = (short) (value ? v | (1 << bit) : v & ~(1 << bit));
		return this;
	}

	@Override
	public BitMask16 setAll(int offset, int length, boolean value) throws IndexOutOfBoundsException {
		if(offset<0||offset>=SIZE){ throw new IndexOutOfBoundsException("Offset "+offset+" out of bounds (0-"+(SIZE-1)+")"); }
		length = Math.max(0, Math.min(length, 8-offset));
		short m = (short) (getFullBits(length) << offset);
		v = (short) (value ? v | m : v & ~m);
		return this;
	}

	@Override
	public BitMask16 setAll(int set_offset, boolean[] values, int value_offset, int value_length) throws IndexOutOfBoundsException {
		if(set_offset<0||set_offset>=SIZE){ throw new IndexOutOfBoundsException("SetOffset "+set_offset+" out of bounds (0-"+(SIZE-1)+")"); }
		if(values!=null&&values.length>0){
			value_offset = Math.max(0, Math.min(value_offset, values.length));
			value_length = Math.max(0, Math.min(value_length, values.length-value_offset));
			if(value_length>0){
				v = (short) (getBits(values, value_offset, value_length) << set_offset);
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
	public BitMask16 toggle(int bit) throws IndexOutOfBoundsException {
		if(bit<0||bit>=SIZE){ throw new IndexOutOfBoundsException("Index "+bit+" out of bounds (0-"+(SIZE-1)+")"); }
		v ^= 1 << bit;
		return this;
	}

	@Override
	public BitMask16 toggleAll(int offset, int length) throws IndexOutOfBoundsException {
		v ^= getFullBits(length) << offset;
		return this;
	}

	@Override
	public String toBinaryString() {
		return toBinary(v, SIZE);
	}
	
	@Override
	protected BitMask16 clone() {
		return new BitMask16(v);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append("{v=").append(v).append("; b=").append(toBinaryString()).append("}").toString();
	}
}
