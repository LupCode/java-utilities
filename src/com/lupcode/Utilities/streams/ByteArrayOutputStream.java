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
		needed += offset;
		if(needed > buffer.length) {
			byte[] newBuf = new byte[needed];
			System.arraycopy(buffer, 0, newBuf, 0, buffer.length);
			this.buffer = newBuf;
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
	 * Discards the first <code>n</code> bytes from the buffer 
	 * and moves the rest of the valid bytes in the buffer 
	 * to the beginning (to position 0 in the array)
	 * @param n Amount of bytes that should be discarded
	 * @throws IndexOutOfBoundsException if <code>n</code> is negative or greater than {@link ByteArrayOutputStream#size()}
	 */
	public void discard(int n) {
		if(n < 0 || n > offset) throw new IndexOutOfBoundsException();
		if(n == 0) return;
		if(n == offset) { offset=0; return; }
		System.arraycopy(buffer, n, buffer, 0, offset-n); // move data left in buffer to beginning
		offset -= n;
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
	
	/**
	 * Creates a {@link ByteArrayInputStream} that contains the data 
	 * that was written to this {@link ByteArrayOutputStream}. 
	 * This stream gets {@link ByteArrayOutputStream#reset()} so 
	 * that it can be written again as it is a new instance.
	 * @return Stream that reads a copy of the data of this stream
	 */
	public ByteArrayInputStream toInputStream() {
		return toInputStream(offset);
	}
	
	/**
	 * Creates a {@link ByteArrayInputStream} that contains the first <code>n</code> bytes  
	 * that were written to this {@link ByteArrayOutputStream}.
	 * Those <code>n</code> are discarded from this {@link ByteArrayOutputStream} 
	 * by calling {@link ByteArrayOutputStream#discard(int)}
	 * @param n Amount of bytes that should be readable in the {@link ByteArrayInputStream} 
	 * and will be discarded from this {@link ByteArrayOutputStream}
	 * @return Stream that reads a copy of the first <code>n</code> bytes of this stream
	 * @throws IndexOutOfBoundsException if <code>n</code> is negative or greater than {@link ByteArrayOutputStream#size()}
	 */
	public synchronized ByteArrayInputStream toInputStream(int n) throws IndexOutOfBoundsException {
		if(n < 0 || n > offset) throw new IndexOutOfBoundsException();
		ByteArrayInputStream input = new ByteArrayInputStream(buffer, 0, n, true); // create copy of data
		discard(n);
		return input;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append("{size=").append(offset).
				append("; capacity=").append(buffer.length).append("; closed=").append(closed).
				append("}").toString();
	}
}
