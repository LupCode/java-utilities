package com.lupcode.Utilities.streams.optional;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Same behavior as the {@link InflaterInputStream} but allows dynamic bypassing of 
 * the {@link Inflater} to that can also be read unchanged
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class OptionalInflaterInputStream extends InputStream {
	
	protected InputStream input;
	protected Inflater inflater;
	protected InflaterInputStream inflaterInput;
	protected int bufferSize = 512;
	protected boolean doInflate = true;
	
	/**
	 * Creates a new {@link OptionalInflaterInputStream} instance that does not
	 * inflate data until it gets enabled
	 * @param input Stream that should be read
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public OptionalInflaterInputStream(InputStream input) throws NullPointerException {
		this(input, null);
		this.doInflate = false;
	}
	
	/**
	 * Creates a new {@link OptionalInflaterInputStream} instance that does inflate 
	 * data if the given {@link Inflater} is not null otherwise it won't inflate data 
	 * until it gets enabled later on
	 * @param input Stream that should be read
	 * @param inflater Inflater that should be used for inflating
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public OptionalInflaterInputStream(InputStream input, Inflater inflater) throws NullPointerException {
		if(input == null) throw new NullPointerException("InputStream cannot be null");
		this.input = input;
		setInflater(inflater);
	}
	
	/**
	 * Creates a new {@link OptionalInflaterInputStream} instance that does inflate 
	 * data if the given {@link Inflater} is not null otherwise it won't inflate data 
	 * until it gets enabled later on
	 * @param input Stream that should be read
	 * @param inflater Inflater that should be used for inflating
	 * @param bufferSize Size of buffer that is used to read inflated data
	 * @throws NullPointerException if {@link InputStream} is null
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public OptionalInflaterInputStream(InputStream input, Inflater inflater, int bufferSize) throws NullPointerException, IllegalArgumentException {
		if(input == null) throw new NullPointerException("InputStream cannot be null");
		this.input = input;
		setInflater(inflater, bufferSize);
	}
	
	/**
	 * Returns if that gets inflated otherwise gets passed unchanged.
	 * @return True if data gets inflated
	 */
	public boolean isInflaterEnabled() {
		return doInflate;
	}
	
	/**
	 * Sets if the inflater should actually be used otherwise
	 * data gets passed unchanged.
	 * @param doInflate If inflater should actually be used
	 */
	public void doInflate(boolean doInflate) {
		this.doInflate = doInflate;
	}
	
	/**
	 * Returns the inflater that is used
	 * @return Inflater used or null if not set
	 */
	public Inflater getInflater() {
		return inflater;
	}
	
	/**
	 * Sets the inflater that should be used. 
	 * Setting an inflater does not automatically enable inflation: 
	 * For that use {@link OptionalInflaterInputStream#doInflate(boolean)} instead
	 * @param inflater Inflater that should be used (if null {@link Inflater} will be used)
	 */
	public void setInflater(Inflater inflater) {
		setInflater(inflater, bufferSize);
	}
	
	/**
	 * Returns the size of the buffer that is used for reading the inflated data
	 * @return Size of buffer used for reading inflated data
	 */
	public int getBufferSize() {
		return bufferSize;
	}
	
	/**
	 * Sets the buffer size that should be used for reading the inflated data
	 * @param bufferSize Size of buffer used for reading inflated data
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public void setBufferSize(int bufferSize) throws IllegalArgumentException {
		setInflater(inflater, bufferSize);
	}
	
	/**
	 * Sets the inflater and buffer size that should be used.
	 * For that use {@link OptionalInflaterInputStream#doInflate(boolean)} instead
	 * @param inflater Inflater that should be used (if null {@link Inflater} will be used)
	 * @param bufferSize Size of buffer used for reading inflated data
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public synchronized void setInflater(Inflater inflater, int bufferSize) throws IllegalArgumentException {
		if(bufferSize < 1) throw new IllegalArgumentException("Buffer size cannot be smaller than 1");
		this.bufferSize = bufferSize;
		this.inflater = inflater;
		this.inflaterInput = new InflaterInputStream(input, inflater!=null ? inflater : new Inflater(), bufferSize);
	}

	@Override
	public int available() throws IOException {
		return doInflate ? inflaterInput.available() : input.available();
	}
	
	@Override
	public void close() throws IOException {
		inflaterInput.close();
		try { input.close(); } catch (Exception ex) {}
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		inflaterInput.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {
		return inflaterInput.markSupported();
	}
	
	@Override
	public int read() throws IOException {
		return doInflate ? inflaterInput.read() : input.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return doInflate ? inflaterInput.read(b) : input.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return doInflate ? inflaterInput.read(b, off, len) : input.read(b, off, len);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		inflaterInput.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return doInflate ? inflaterInput.skip(n) : input.skip(n);
	}
}
