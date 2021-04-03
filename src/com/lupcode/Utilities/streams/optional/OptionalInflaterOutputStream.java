package com.lupcode.Utilities.streams.optional;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

/**
 * Same behavior as the {@link InflaterOutputStream} but allows dynamic bypassing of 
 * the {@link Inflater} to that can also be written unchanged
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class OptionalInflaterOutputStream extends OutputStream {
	
	protected OutputStream output;
	protected Inflater inflater;
	protected InflaterOutputStream inflaterOutput;
	protected int bufferSize = 512;
	protected boolean doInflate = true;
	
	/**
	 * Creates a new {@link OptionalInflaterOutputStream} instance that does not
	 * inflate data until it gets enabled
	 * @param output Stream that should be written to
	 * @throws NullPointerException if {@link OutputStream} is null
	 */
	public OptionalInflaterOutputStream(OutputStream output) throws NullPointerException {
		this(output, null);
		this.doInflate = false;
	}
	
	/**
	 * Creates a new {@link OptionalInflaterOutputStream} instance that does inflate 
	 * data if the given {@link Inflater} is not null otherwise it won't inflate data 
	 * until it gets enabled later on
	 * @param output Stream that should be written to
	 * @param inflater Inflater that should be used for inflating
	 * @throws NullPointerException if {@link OutputStream} is null
	 */
	public OptionalInflaterOutputStream(OutputStream output, Inflater inflater) throws NullPointerException {
		if(output == null) throw new NullPointerException("OutputStream cannot be null");
		this.output = output;
		setInflater(inflater);
	}
	
	/**
	 * Creates a new {@link OptionalInflaterOutputStream} instance that does inflate 
	 * data if the given {@link Inflater} is not null otherwise it won't inflate data 
	 * until it gets enabled later on
	 * @param output Stream that should be written to
	 * @param inflater Inflater that should be used for inflating
	 * @param bufferSize Size of buffer that is used to write inflated data
	 * @throws NullPointerException if {@link OutputStream} is null
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public OptionalInflaterOutputStream(OutputStream output, Inflater inflater, int bufferSize) throws NullPointerException, IllegalArgumentException {
		if(output == null) throw new NullPointerException("OutputStream cannot be null");
		this.output = output;
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
	 * Setting a inflater does not automatically enable inflation: 
	 * For that use {@link OptionalInflaterOutputStream#doInflate(boolean)} instead
	 * @param inflater Inflater that should be used (if null {@link Inflater} will be used)
	 */
	public void setInflater(Inflater inflater) {
		setInflater(inflater, bufferSize);
	}
	
	/**
	 * Returns the size of the buffer that is used for writing the inflated data
	 * @return Size of buffer used for writing inflated data
	 */
	public int getBufferSize() {
		return bufferSize;
	}
	
	/**
	 * Sets the buffer size that should be used for writing the inflated data
	 * @param bufferSize Size of buffer used for writing inflated data
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public void setBufferSize(int bufferSize) throws IllegalArgumentException {
		setInflater(inflater, bufferSize);
	}
	
	/**
	 * Sets the inflater and buffer size that should be used.
	 * For that use {@link OptionalInflaterOutputStream#doInflate(boolean)} instead
	 * @param inflater Inflater that should be used (if null {@link Inflater} will be used)
	 * @param bufferSize Size of buffer used for writing inflated data
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public synchronized void setInflater(Inflater inflater, int bufferSize) throws IllegalArgumentException {
		if(bufferSize < 1) throw new IllegalArgumentException("Buffer size cannot be smaller than 1");
		this.bufferSize = bufferSize;
		this.inflater = inflater;
		this.inflaterOutput = new InflaterOutputStream(output, inflater!=null ? inflater : new Inflater(), bufferSize);
	}
	
	@Override
	public void close() throws IOException {
		inflaterOutput.close();
		try { output.close(); } catch (Exception ex) {}
	}

	@Override
	public void flush() throws IOException {
		inflaterOutput.flush();
		try { output.flush(); } catch (Exception ex) {}
	}
	
	@Override
	public void write(int b) throws IOException {
		if(doInflate) inflaterOutput.write(b); else output.write(b);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		if(doInflate) inflaterOutput.write(b); else output.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if(doInflate) inflaterOutput.write(b, off, len); else output.write(b, off, len);
	}
}
