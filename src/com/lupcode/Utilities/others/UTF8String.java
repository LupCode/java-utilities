package com.lupcode.Utilities.others;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Queue;

import com.lupcode.Utilities.streams.UTF8CharInputStream;

/**
 * UTF-8 String holds UTF-8 characters
 * @author LupCode.com (Luca Vogels)
 * @since 2020-12-23
 */
public class UTF8String extends ArrayList<String> implements Queue<String> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an empty UTF-8 string
	 */
	public UTF8String() {
		super();
	}
	
	/**
	 * Creates an UTF-8 string based on a normal string
	 * @param utf8String String that should be used for initialization
	 */
	public UTF8String(String utf8String) {
		super();
		if(utf8String!=null) appendString(utf8String);
	}
	
	/**
	 * Creates an empty UTF-8 string with an initial capacity
	 * @param initialCapacity Initial capacity
	 */
	public UTF8String(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Creates an UTF-8 string based on a given list of UTF-8 characters (not further proofed)
	 * @param chars UTF-8 characters
	 */
	protected UTF8String(Collection<String> chars) {
		super(chars);
	}
	
	/**
	 * How many characters the UTF-8 string holds
	 * @return Amount of characters
	 */
	public int length() {
		return size();
	}

	/**
	 * Returns a single UTF-8 character at a given index
	 * @param index Index of UTF-8 character that should be returned
	 * @return UTF-8 character
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 */
	public String charAt(int index) throws IndexOutOfBoundsException {
		return get(index);
	}
	
	/**
	 * Returns a substring
	 * @param beginIndex Index where substring should begin (inclusive)
	 * @return Substring
	 * @throws IndexOutOfBoundsException if beginIndex is out of bounds
	 */
	public UTF8String substring(int beginIndex) throws IndexOutOfBoundsException {
		return new UTF8String(subList(beginIndex, size()));
	}
	
	/**
	 * Returns a substring
	 * @param beginIndex Index where substring should begin (inclusive)
	 * @param endIndex Index where substring should end (exclusive)
	 * @return Substring
	 * @throws IndexOutOfBoundsException if beginIndex or endIndex is out of bounds
	 */
	public UTF8String substring(int beginIndex, int endIndex) {
		return new UTF8String(subList(beginIndex, endIndex));
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Collection)) return false;
		try {
			@SuppressWarnings("unchecked")
			Collection<String> c = (Collection<String>) o;
			if(c.size() != super.size()) return false;
			int index = 0;
			for(String str : c) {
				if(str==null || !Arrays.equals(
						str.getBytes(StandardCharsets.UTF_8), 
						super.get(index++).getBytes(StandardCharsets.UTF_8)
					)) return false;
			}
			return true;
		} catch (ClassCastException ex) { return false; }
	}
	
	/**
	 * Sets the content of this UTF-8 string from a given normal String
	 * @param utf8String String that should be read
	 */
	public void setString(String utf8String) {
		if(utf8String==null) throw new NullPointerException("String cannot be null");
		clear();
		appendString(utf8String);
	}
	
	/**
	 * Appends a normal string to this UTF-8 string
	 * @param utf8String String that should be appended
	 */
	public void appendString(String utf8String) {
		if(utf8String==null) throw new NullPointerException("String cannot be null");
		UTF8CharInputStream input = new UTF8CharInputStream(new ByteArrayInputStream(utf8String.getBytes(StandardCharsets.UTF_8)));
		String c;
		try {
			while((c = input.readChar())!=null) add(c);
		} catch (IOException e) {}
	}
	
	/**
	 * Returns byte array containing the UTF-8 bytes of the UTF-8 string
	 * @return UTF-8 byte array
	 */
	public byte[] getBytes() {
		return toString().getBytes(StandardCharsets.UTF_8);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String c : this) {
			sb.append(c);
		} return sb.toString();
	}
	
	
	@Override
	public boolean offer(String e) {
		if(e==null) throw new NullPointerException("Char cannot be null");
		return super.add(e);
	}
	@Override
	public String remove() {
		if(isEmpty()) throw new NoSuchElementException("UTF8String is empty");
		return super.remove(0);
	}
	@Override
	public String poll() {
		return isEmpty() ? null : super.remove(0);
	}
	@Override
	public String element() {
		if(isEmpty()) throw new NoSuchElementException("UTF8String is empty");
		return super.get(0);
	}
	@Override
	public String peek() {
		return isEmpty() ? null : super.get(0);
	}
}
