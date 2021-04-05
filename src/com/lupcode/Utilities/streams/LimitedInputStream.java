package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link InputStream} that allows to dynamically set/unset a limit at which 
 * the {@link LimitedInputStream#read()} will return the value -2
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-05
 */
public class LimitedInputStream extends InputStream {

	protected InputStream input;
	protected long remaining;
	
	/**
	 * Creates a new {@link LimitedInputStream} without a limit set yet
	 * @param input {@link InputStream} used as input
	 */
	public LimitedInputStream(InputStream input){
		this(input, -1);
	}
	
	/**
	 * Creates a new {@link LimitedInputStream} with a given 
	 * limit already set
	 * @param input {@link InputStream} used as input
	 * @param remaining Amount of bytes that can be read until limit is 
	 * reached and {@link LimitedInputStream#read()} returns -2 (negative means no limit)
	 */
	public LimitedInputStream(InputStream input, long remaining){
		if(input==null){ throw new NullPointerException("InputStream cannot be null"); }
		this.input = input;
		remaining(remaining);
	}
	
	/**
	 * @return True if currently a limit is set
	 */
	public boolean hasLimit() {
		return remaining >= 0;
	}
	
	/**
	 * Sets a limit
	 * @param remaining Amount of bytes that can be read until limit is 
	 * reached and {@link LimitedInputStream#read()} returns -2 (negative means no limit)
	 */
	public void remaining(long remaining) {
		this.remaining = Math.max(-1, remaining);
	}
	
	/**
	 * Returns how many bytes can be read until limit is reached at which 
	 * the {@link LimitedInputStream#read()} only returns -2.
	 * Negative values means no limit set
	 * @return Amount of readable bytes or negative if no limit
	 */
	public long remaining() {
		return remaining;
	}
	
	/**
	 * Removes the currently set limit
	 */
	public void removeLimit() {
		remaining = -1;
	}
	
	@Override
	public int available() throws IOException {
		return remaining >= 0 ? Math.min(input.available(), (int)Math.min(remaining, Integer.MAX_VALUE)) : input.available();
	}
	
	@Override
	public void close() throws IOException {
		input.close();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		input.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {
		return input.markSupported();
	}

	@Override
	public int read() throws IOException {
		if(remaining == 0) return -2;
		if(remaining > 0) remaining--;
		return input.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(off < 0 || off+len > b.length) throw new IndexOutOfBoundsException();
		len = (int) (remaining >= 0 ? Math.min(len, remaining) : len);
		return input.read(b, off, len);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		input.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		n = remaining >= 0 ? Math.min(n, remaining) : n;
		n = input.skip(n);
		if(remaining > 0) remaining -= Math.min(n, remaining);
		return n;
	}
}