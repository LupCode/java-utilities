package com.lupcode.Utilities.streams.optional;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Same behavior as the {@link DeflaterOutputStream} but allows dynamic bypassing of 
 * the {@link Deflater} to that can also be written unchanged
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class OptionalDeflaterOutputStream extends OutputStream {
	
	protected OutputStream output;
	protected Deflater deflater;
	protected DeflaterOutputStream deflaterOutput;
	protected int bufferSize = 512;
	protected boolean doDeflate = true;
	
	/**
	 * Creates a new {@link OptionalDeflaterOutputStream} instance that does not
	 * deflate data until it gets enabled
	 * @param output Stream that should be written to
	 * @throws NullPointerException if {@link OutputStream} is null
	 */
	public OptionalDeflaterOutputStream(OutputStream output) throws NullPointerException {
		this(output, null);
		this.doDeflate = false;
	}
	
	/**
	 * Creates a new {@link OptionalDeflaterOutputStream} instance that does deflate 
	 * data if the given {@link Deflater} is not null otherwise it won't deflate data 
	 * until it gets enabled later on
	 * @param output Stream that should be written to
	 * @param deflater Deflater that should be used for deflating
	 * @throws NullPointerException if {@link OutputStream} is null
	 */
	public OptionalDeflaterOutputStream(OutputStream output, Deflater deflater) throws NullPointerException {
		setOutputStream(output);
		setDeflater(deflater);
	}
	
	/**
	 * Creates a new {@link OptionalDeflaterOutputStream} instance that does deflate 
	 * data if the given {@link Deflater} is not null otherwise it won't deflate data 
	 * until it gets enabled later on
	 * @param output Stream that should be written to
	 * @param deflater Deflater that should be used for deflating
	 * @param bufferSize Size of buffer that is used to write inflated data
	 * @throws NullPointerException if {@link OutputStream} is null
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public OptionalDeflaterOutputStream(OutputStream output, Deflater deflater, int bufferSize) throws NullPointerException, IllegalArgumentException {
		setOutputStream(output);
		setDeflater(deflater, bufferSize);
	}
	
	/**
	 * Returns the {@link OutputStream} that is used as output of the deflater
	 * @return {@link OutputStream} used as output
	 */
	public OutputStream getOutputStream() {
		return output;
	}
	
	/**
	 * Sets the {@link OutputStream} that is used as output of the deflater
	 * @param output Stream used as output for the deflater
	 * @throws NullPointerException if {@link OutputStream} is null
	 */
	public synchronized void setOutputStream(OutputStream output) throws NullPointerException {
		if(output == null) throw new NullPointerException("OutputStream cannot be null");
		this.output = output;
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
	 * For that use {@link OptionalDeflaterOutputStream#doDeflate(boolean)} instead
	 * @param deflater Deflater that should be used (if null {@link Deflater} will be used)
	 */
	public void setDeflater(Deflater deflater) {
		setDeflater(deflater, bufferSize);
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
		setDeflater(deflater, bufferSize);
	}
	
	/**
	 * Sets the deflater and buffer size that should be used.
	 * For that use {@link OptionalDeflaterOutputStream#doDeflate(boolean)} instead
	 * @param deflater Deflater that should be used (if null {@link Deflater} will be used)
	 * @param bufferSize Size of buffer used for writing inflated data
	 * @throws IllegalArgumentException if bufferSize < 1
	 */
	public synchronized void setDeflater(Deflater deflater, int bufferSize) throws IllegalArgumentException {
		if(bufferSize < 1) throw new IllegalArgumentException("Buffer size cannot be smaller than 1");
		this.bufferSize = bufferSize;
		this.deflater = deflater;
		this.deflaterOutput = new DeflaterOutputStream(output, deflater!=null ? deflater : new Deflater(), bufferSize);
	}
	
	@Override
	public void close() throws IOException {
		deflaterOutput.close();
		try { output.close(); } catch (Exception ex) {}
	}

	@Override
	public void flush() throws IOException {
		deflaterOutput.flush();
		try { output.flush(); } catch (Exception ex) {}
	}
	
	@Override
	public void write(int b) throws IOException {
		if(doDeflate) deflaterOutput.write(b); else output.write(b);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		if(doDeflate) deflaterOutput.write(b); else output.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if(doDeflate) deflaterOutput.write(b, off, len); else output.write(b, off, len);
	}
}
