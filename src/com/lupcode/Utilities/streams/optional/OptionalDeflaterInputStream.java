package com.lupcode.Utilities.streams.optional;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

/**
 * Same behavior as the {@link DeflaterInputStream} but allows dynamic bypassing of 
 * the {@link Deflater} to that can also be read unchanged
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class OptionalDeflaterInputStream extends InputStream {
	
	protected InputStream input;
	protected Deflater deflater;
	protected DeflaterInputStream deflaterInput;
	protected int bufferSize = 512;
	protected boolean doDeflate = true;
	
	/**
	 * Creates a new {@link OptionalDeflaterInputStream} instance that does not
	 * deflate data until it gets enabled
	 * @param input Stream that should be read
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public OptionalDeflaterInputStream(InputStream input) throws NullPointerException {
		this(input, null);
		this.doDeflate = false;
	}
	
	/**
	 * Creates a new {@link OptionalDeflaterInputStream} instance that does deflate 
	 * data if the given {@link Deflater} is not null otherwise it won't deflate data 
	 * until it gets enabled later on
	 * @param input Stream that should be read
	 * @param deflater Deflater that should be used for deflating
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public OptionalDeflaterInputStream(InputStream input, Deflater deflater) throws NullPointerException {
		if(input == null) throw new NullPointerException("InputStream cannot be null");
		this.input = input;
		setDeflater(deflater);
	}
	
	/**
	 * Creates a new {@link OptionalDeflaterInputStream} instance that does deflate 
	 * data if the given {@link Deflater} is not null otherwise it won't deflate data 
	 * until it gets enabled later on
	 * @param input Stream that should be read
	 * @param deflater Deflater that should be used for deflating
	 * @param bufferSize Size of buffer that is used to read inflated data
	 * @throws NullPointerException if {@link InputStream} is null
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public OptionalDeflaterInputStream(InputStream input, Deflater deflater, int bufferSize) throws NullPointerException, IllegalArgumentException {
		if(input == null) throw new NullPointerException("InputStream cannot be null");
		this.input = input;
		setDeflater(deflater, bufferSize);
	}
	
	/**
	 * Returns if that gets deflated otherwise gets passed unchanged.
	 * @return True if data gets deflated
	 */
	public boolean isDeflaterEnabled() {
		return doDeflate;
	}
	
	/**
	 * Sets if the deflater should actually be used otherwise
	 * data gets passed unchanged.
	 * @param doDeflate If deflater should actually be used
	 */
	public void doDeflate(boolean doDeflate) {
		this.doDeflate = doDeflate;
	}
	
	/**
	 * Returns the deflater that is used
	 * @return Deflater used or null if not set
	 */
	public Deflater getDeflater() {
		return deflater;
	}
	
	/**
	 * Sets the deflater that should be used. 
	 * Setting a deflater does not automatically enable deflation: 
	 * For that use {@link OptionalDeflaterInputStream#doDeflate(boolean)} instead
	 * @param deflater Deflater that should be used (if null {@link Deflater} will be used)
	 */
	public void setDeflater(Deflater deflater) {
		setDeflater(deflater, bufferSize);
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
		setDeflater(deflater, bufferSize);
	}
	
	/**
	 * Sets the deflater and buffer size that should be used.
	 * For that use {@link OptionalDeflaterInputStream#doDeflate(boolean)} instead
	 * @param deflater Deflater that should be used (if null {@link Deflater} will be used)
	 * @param bufferSize Size of buffer used for reading inflated data
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public synchronized void setDeflater(Deflater deflater, int bufferSize) throws IllegalArgumentException {
		if(bufferSize < 1) throw new IllegalArgumentException("Buffer size cannot be smaller than 1");
		this.bufferSize = bufferSize;
		this.deflater = deflater;
		this.deflaterInput = new DeflaterInputStream(input, deflater!=null ? deflater : new Deflater(), bufferSize);
	}

	@Override
	public int available() throws IOException {
		return doDeflate ? deflaterInput.available() : input.available();
	}
	
	@Override
	public void close() throws IOException {
		deflaterInput.close();
		try { input.close(); } catch (Exception ex) {}
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		deflaterInput.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {
		return deflaterInput.markSupported();
	}
	
	@Override
	public int read() throws IOException {
		return doDeflate ? deflaterInput.read() : input.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return doDeflate ? deflaterInput.read(b) : input.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return doDeflate ? deflaterInput.read(b, off, len) : input.read(b, off, len);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		deflaterInput.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return doDeflate ? deflaterInput.skip(n) : input.skip(n);
	}
}
