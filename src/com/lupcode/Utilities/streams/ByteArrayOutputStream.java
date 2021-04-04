package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Same behavior as a {@link java.io.ByteArrayOutputStream} but allows 
 * direct access/manipulation of the buffer array
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class ByteArrayOutputStream extends OutputStream {
	
	protected byte[] buffer;
	protected int offset = 0;
	protected boolean closed = false;
	
	/**
	 * Creates a new {@link ByteArrayInputStream} instance 
	 * with an initial buffer size of zero
	 */
	public ByteArrayOutputStream() {
		this(0);
	}
	
	/**
	 * Creates a new {@link ByteArrayInputStream} instance
	 * @param initialCapacity Initial size of the byte array used to store the written data
	 */
	public ByteArrayOutputStream(int initialCapacity) {
		this.buffer = new byte[Math.max(0, initialCapacity)];
	}
	
	protected void checkCapacity(int needed) {
		needed = buffer.length - offset + needed;
		if(needed > buffer.length) {
			byte[] newBuf = new byte[needed];
			System.arraycopy(buffer, 0, newBuf, 0, buffer.length);
		}
	}
	
	/**
	 * New writes will start at the beginning of the byte buffer 
	 * and will overwrite already previously written data
	 */
	public void reset() {
		offset = 0;
	}
	
	/**
	 * @return How many bytes are stored in the buffer
	 */
	public int size() {
		return offset;
	}
	
	/**
	 * @return Current size of the array 
	 */
	public int capacity() {
		return buffer.length;
	}
	
	/**
	 * @return Raw buffer (no copy)
	 */
	public byte[] array() {
		return buffer;
	}

	/**
	 * @return Copy of buffer
	 */
	public byte[] toByteArray() {
		byte[] clone = new byte[buffer.length];
		System.arraycopy(buffer, 0, clone, 0, offset);
		return clone;
	}
	
	@Override
	public ByteArrayOutputStream clone() {
		ByteArrayOutputStream clone = new ByteArrayOutputStream(buffer.length);
		clone.offset = offset;
		clone.closed = closed;
		System.arraycopy(buffer, 0, clone.buffer, 0, buffer.length);
		return clone;
	}
	
	/**
	 * @return True if stream has been closed
	 */
	public boolean isClosed() {
		return closed;
	}
	
	/**
	 * Re-opens stream so it can be used with 
	 * the same state before it got closed
	 */
	public void reopen() {
		closed = false;
	}
	
	@Override
	public void close() {
		closed = true;
	}
	
	@Override
	public void flush() {
		
	}
	
	@Override
	public void write(int b) throws IOException {
		if(closed) throw new IOException(getClass().getSimpleName()+" already closed");
		checkCapacity(1);
		buffer[offset] = (byte)b;
		offset++;
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if(closed) throw new IOException(getClass().getSimpleName()+" already closed");
		checkCapacity(len);
		System.arraycopy(b, off, buffer, offset, len);
		offset += len;
	}
	
	/**
	 * Writes the stored data to a given {@link OutputStream}
	 * @param output Stream the data should be written to
	 * @throws IOException If writing fails
	 */
	public void writeTo(OutputStream output) throws IOException {
		output.write(buffer, 0, offset);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append("{size=").append(offset).
				append("; capacity=").append(buffer.length).append("; closed=").append(closed).
				append("}").toString();
	}
}
